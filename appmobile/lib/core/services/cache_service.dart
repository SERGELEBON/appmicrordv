import 'dart:async';
import 'dart:convert';

class CachedResponse<T> {
  final T data;
  final DateTime timestamp;
  final Duration ttl;

  CachedResponse({
    required this.data,
    required this.timestamp,
    required this.ttl,
  });

  bool get isExpired => DateTime.now().isAfter(timestamp.add(ttl));
}

class CacheService {
  static final CacheService _instance = CacheService._internal();
  factory CacheService() => _instance;
  CacheService._internal();

  final Map<String, CachedResponse> _memoryCache = {};
  Timer? _cleanupTimer;

  // TTL par défaut pour différents types de données
  static const Duration _defaultTtl = Duration(minutes: 5);
  static const Duration _shortTtl = Duration(minutes: 1);
  static const Duration _longTtl = Duration(hours: 1);

  void startPeriodicCleanup() {
    _cleanupTimer?.cancel();
    _cleanupTimer = Timer.periodic(const Duration(minutes: 10), (_) {
      _cleanup();
    });
  }

  void _cleanup() {
    final keysToRemove = <String>[];
    
    for (final entry in _memoryCache.entries) {
      if (entry.value.isExpired) {
        keysToRemove.add(entry.key);
      }
    }
    
    for (final key in keysToRemove) {
      _memoryCache.remove(key);
    }
  }

  String _generateKey(String endpoint, Map<String, dynamic>? params) {
    final baseKey = endpoint;
    if (params == null || params.isEmpty) return baseKey;
    
    final sortedParams = Map.fromEntries(
      params.entries.toList()..sort((a, b) => a.key.compareTo(b.key))
    );
    
    return '$baseKey?${Uri(queryParameters: sortedParams.map((k, v) => MapEntry(k, v.toString()))).query}';
  }

  T? get<T>(String endpoint, {Map<String, dynamic>? params}) {
    final key = _generateKey(endpoint, params);
    final cached = _memoryCache[key];
    
    if (cached != null && !cached.isExpired) {
      return cached.data as T;
    }
    
    if (cached != null && cached.isExpired) {
      _memoryCache.remove(key);
    }
    
    return null;
  }

  void set<T>(
    String endpoint, 
    T data, {
    Map<String, dynamic>? params,
    Duration? ttl,
    CacheStrategy strategy = CacheStrategy.normal,
  }) {
    final key = _generateKey(endpoint, params);
    final cacheTtl = ttl ?? _getTtlForStrategy(strategy);
    
    _memoryCache[key] = CachedResponse<T>(
      data: data,
      timestamp: DateTime.now(),
      ttl: cacheTtl,
    );
  }

  Duration _getTtlForStrategy(CacheStrategy strategy) {
    switch (strategy) {
      case CacheStrategy.short:
        return _shortTtl;
      case CacheStrategy.long:
        return _longTtl;
      case CacheStrategy.normal:
        return _defaultTtl;
    }
  }

  void invalidate(String endpoint, {Map<String, dynamic>? params}) {
    final key = _generateKey(endpoint, params);
    _memoryCache.remove(key);
  }

  void invalidatePattern(String pattern) {
    final keysToRemove = _memoryCache.keys
        .where((key) => key.contains(pattern))
        .toList();
    
    for (final key in keysToRemove) {
      _memoryCache.remove(key);
    }
  }

  void clear() {
    _memoryCache.clear();
  }

  void dispose() {
    _cleanupTimer?.cancel();
    _memoryCache.clear();
  }

  // Statistiques du cache pour le debugging
  Map<String, dynamic> get stats => {
    'totalEntries': _memoryCache.length,
    'expiredEntries': _memoryCache.values.where((v) => v.isExpired).length,
    'validEntries': _memoryCache.values.where((v) => !v.isExpired).length,
  };
}

enum CacheStrategy {
  short,  // 1 minute - données qui changent souvent
  normal, // 5 minutes - données moyennement stables
  long,   // 1 heure - données stables (ex: spécialités médicales)
}
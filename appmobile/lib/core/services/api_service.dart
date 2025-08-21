import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../constants/app_constants.dart';
import 'cache_service.dart';

class ApiService {
  late final Dio _dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  final CacheService _cache = CacheService();

  ApiService() {
    // Optimisation : Augmentation des timeouts pour les connexions lentes
    _dio = Dio(BaseOptions(
      baseUrl: AppConstants.baseUrl,
      connectTimeout: const Duration(seconds: 30), // Augmenté de 10s à 30s
      receiveTimeout: const Duration(seconds: 30),  // Augmenté de 10s à 30s
      sendTimeout: const Duration(seconds: 30),     // Ajouté pour les uploads
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await _storage.read(key: AppConstants.tokenKey);
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) async {
        if (error.response?.statusCode == 401) {
          await _refreshToken();
          final token = await _storage.read(key: AppConstants.tokenKey);
          if (token != null) {
            error.requestOptions.headers['Authorization'] = 'Bearer $token';
            final response = await _dio.fetch(error.requestOptions);
            handler.resolve(response);
          }
        }
        handler.next(error);
      },
    ));
  }

  Future<void> _refreshToken() async {
    try {
      final refreshToken = await _storage.read(key: AppConstants.refreshTokenKey);
      if (refreshToken != null) {
        final response = await _dio.post(
          '${AppConstants.authServiceUrl}${AppConstants.refreshTokenEndpoint}',
          data: {'refreshToken': refreshToken},
        );
        
        if (response.statusCode == 200) {
          final newToken = response.data['accessToken'];
          await _storage.write(key: AppConstants.tokenKey, value: newToken);
        }
      }
    } catch (e) {
      await _storage.deleteAll();
    }
  }

  Dio get dio => _dio;

  Future<void> setAuthToken(String token) async {
    await _storage.write(key: AppConstants.tokenKey, value: token);
  }

  Future<void> setRefreshToken(String token) async {
    await _storage.write(key: AppConstants.refreshTokenKey, value: token);
  }

  Future<void> clearTokens() async {
    await _storage.delete(key: AppConstants.tokenKey);
    await _storage.delete(key: AppConstants.refreshTokenKey);
  }

  Future<String?> getToken() async {
    return await _storage.read(key: AppConstants.tokenKey);
  }

  // Méthodes de cache optimisées
  Future<T> getCachedOrFetch<T>(
    String endpoint, {
    Map<String, dynamic>? queryParams,
    CacheStrategy strategy = CacheStrategy.normal,
    T Function(Map<String, dynamic>)? fromJson,
    bool forceRefresh = false,
  }) async {
    // Vérifier le cache d'abord (sauf si forceRefresh)
    if (!forceRefresh) {
      final cached = _cache.get<T>(endpoint, params: queryParams);
      if (cached != null) {
        return cached;
      }
    }

    // Faire la requête réseau
    final response = await _dio.get(endpoint, queryParameters: queryParams);
    
    T data;
    if (fromJson != null) {
      data = fromJson(response.data);
    } else {
      data = response.data as T;
    }

    // Mettre en cache le résultat
    _cache.set(endpoint, data, params: queryParams, strategy: strategy);
    
    return data;
  }

  Future<T> postWithCache<T>(
    String endpoint, {
    dynamic data,
    Map<String, dynamic>? queryParams,
    CacheStrategy strategy = CacheStrategy.short,
    T Function(Map<String, dynamic>)? fromJson,
  }) async {
    final response = await _dio.post(endpoint, data: data, queryParameters: queryParams);
    
    T result;
    if (fromJson != null) {
      result = fromJson(response.data);
    } else {
      result = response.data as T;
    }

    // Cache les résultats POST avec TTL court par défaut
    _cache.set(endpoint, result, params: queryParams, strategy: strategy);
    
    return result;
  }

  void invalidateCache(String pattern) {
    _cache.invalidatePattern(pattern);
  }

  void clearCache() {
    _cache.clear();
  }

  void dispose() {
    _cache.dispose();
  }
}
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/user.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';
import '../constants/app_constants.dart';
import 'dart:convert';

final apiServiceProvider = Provider<ApiService>((ref) => ApiService());

final authServiceProvider = Provider<AuthService>((ref) {
  final apiService = ref.read(apiServiceProvider);
  return AuthService(apiService.dio);
});

final authStateProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  final authService = ref.read(authServiceProvider);
  final apiService = ref.read(apiServiceProvider);
  return AuthNotifier(authService, apiService);
});

class AuthState {
  final User? user;
  final bool isAuthenticated;
  final bool isLoading;
  final String? error;

  const AuthState({
    this.user,
    this.isAuthenticated = false,
    this.isLoading = false,
    this.error,
  });

  AuthState copyWith({
    User? user,
    bool? isAuthenticated,
    bool? isLoading,
    String? error,
  }) {
    return AuthState(
      user: user ?? this.user,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthService _authService;
  final ApiService _apiService;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  AuthNotifier(this._authService, this._apiService) : super(const AuthState()) {
    _checkAuthStatus();
  }

  Future<void> _checkAuthStatus() async {
    state = state.copyWith(isLoading: true);
    
    try {
      final token = await _storage.read(key: AppConstants.tokenKey);
      final userDataString = await _storage.read(key: AppConstants.userDataKey);
      
      if (token != null && userDataString != null) {
        final userData = json.decode(userDataString);
        final user = User.fromJson(userData);
        state = state.copyWith(
          user: user,
          isAuthenticated: true,
          isLoading: false,
        );
      } else {
        state = state.copyWith(isLoading: false);
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors de la vérification de l\'authentification',
      );
    }
  }

  Future<bool> login(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final request = LoginRequest(email: email, password: password);
      final response = await _authService.login(request);
      
      await _apiService.setAuthToken(response.accessToken);
      await _apiService.setRefreshToken(response.refreshToken);
      await _storage.write(
        key: AppConstants.userDataKey,
        value: json.encode(response.user.toJson()),
      );
      
      state = state.copyWith(
        user: response.user,
        isAuthenticated: true,
        isLoading: false,
      );
      
      return true;
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Email ou mot de passe incorrect',
      );
      return false;
    }
  }

  Future<bool> register(RegisterRequest request) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final response = await _authService.register(request);
      
      // Ne pas authentifier immédiatement - attendre la vérification email
      // await _apiService.setAuthToken(response.accessToken);
      // await _apiService.setRefreshToken(response.refreshToken);
      // await _storage.write(
      //   key: AppConstants.userDataKey,
      //   value: json.encode(response.user.toJson()),
      // );
      
      state = state.copyWith(
        isLoading: false,
        // Ne pas marquer comme authentifié - attendre vérification email
        // user: response.user,
        // isAuthenticated: true,
      );
      
      return true;
    } catch (e) {
      String errorMessage = 'Erreur lors de l\'inscription';
      
      // Analyser l'erreur pour donner un message plus précis
      if (e.toString().contains('email')) {
        errorMessage = 'Cette adresse email est déjà utilisée';
      } else if (e.toString().contains('network')) {
        errorMessage = 'Erreur de connexion. Vérifiez votre internet';
      } else if (e.toString().contains('400')) {
        errorMessage = 'Données invalides. Vérifiez vos informations';
      }
      
      state = state.copyWith(
        isLoading: false,
        error: errorMessage,
      );
      return false;
    }
  }

  Future<void> logout() async {
    try {
      await _authService.logout();
    } catch (e) {
      // Continue même si l'appel API échoue
    }
    
    await _apiService.clearTokens();
    await _storage.delete(key: AppConstants.userDataKey);
    
    state = const AuthState();
  }

  Future<bool> verifyEmail(String token) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final response = await _authService.verifyEmail({'token': token});
      
      await _apiService.setAuthToken(response.accessToken);
      await _apiService.setRefreshToken(response.refreshToken);
      await _storage.write(
        key: AppConstants.userDataKey,
        value: json.encode(response.user.toJson()),
      );
      
      state = state.copyWith(
        user: response.user,
        isAuthenticated: true,
        isLoading: false,
      );
      
      return true;
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'Erreur lors de la vérification de l\'email',
      );
      return false;
    }
  }

  Future<bool> forgotPassword(String email) async {
    try {
      await _authService.forgotPassword({'email': email});
      return true;
    } catch (e) {
      state = state.copyWith(
        error: 'Erreur lors de l\'envoi de l\'email de réinitialisation',
      );
      return false;
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }
}
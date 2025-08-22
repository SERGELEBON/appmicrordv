import '../models/user.dart';

class AuthService {
  
  Future<AuthResponse> login(LoginRequest request) async {
    // Simulation d'une connexion réussie
    await Future.delayed(const Duration(seconds: 1));
    
    return const AuthResponse(
      accessToken: 'fake_access_token',
      refreshToken: 'fake_refresh_token',
      user: User(
        id: 1,
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'patient',
      ),
    );
  }

  Future<AuthResponse> register(RegisterRequest request) async {
    await Future.delayed(const Duration(seconds: 1));
    
    return AuthResponse(
      accessToken: 'fake_access_token',
      refreshToken: 'fake_refresh_token',
      user: User(
        id: 2,
        email: request.email,
        firstName: request.firstName,
        lastName: request.lastName,
        role: request.role,
      ),
    );
  }

  Future<AuthResponse> refreshToken(Map<String, String> refreshToken) async {
    await Future.delayed(const Duration(milliseconds: 500));
    
    return const AuthResponse(
      accessToken: 'new_fake_access_token',
      refreshToken: 'new_fake_refresh_token',
      user: User(
        id: 1,
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'patient',
      ),
    );
  }

  Future<void> logout() async {
    await Future.delayed(const Duration(milliseconds: 500));
  }

  Future<User> getProfile() async {
    await Future.delayed(const Duration(milliseconds: 500));
    
    return const User(
      id: 1,
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      role: 'patient',
      phone: '+33123456789',
    );
  }

  Future<User> updateProfile(Map<String, dynamic> userData) async {
    await Future.delayed(const Duration(seconds: 1));
    
    return User(
      id: 1,
      email: userData['email'] ?? 'test@example.com',
      firstName: userData['firstName'] ?? 'John',
      lastName: userData['lastName'] ?? 'Doe',
      role: 'patient',
      phone: userData['phone'],
    );
  }

  Future<void> forgotPassword(Map<String, String> email) async {
    await Future.delayed(const Duration(seconds: 1));
  }

  Future<void> resetPassword(Map<String, String> resetData) async {
    await Future.delayed(const Duration(seconds: 1));
  }

  Future<AuthResponse> verifyEmail(Map<String, String> verificationData) async {
    await Future.delayed(const Duration(seconds: 1));
    
    return const AuthResponse(
      accessToken: 'verified_access_token',
      refreshToken: 'verified_refresh_token',
      user: User(
        id: 1,
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'patient',
      ),
    );
  }
}
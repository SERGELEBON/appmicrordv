import 'package:retrofit/retrofit.dart';
import 'package:dio/dio.dart';
import '../models/user.dart';
import '../constants/app_constants.dart';

part 'auth_service.g.dart';

@RestApi(baseUrl: AppConstants.baseUrl)
abstract class AuthService {
  factory AuthService(Dio dio, {String baseUrl}) = _AuthService;

  @POST('/auth/login')
  Future<AuthResponse> login(@Body() LoginRequest request);

  @POST('/auth/register')
  Future<AuthResponse> register(@Body() RegisterRequest request);

  @POST('/auth/refresh')
  Future<AuthResponse> refreshToken(@Body() Map<String, String> refreshToken);

  @POST('/auth/logout')
  Future<void> logout();

  @GET('/auth/profile')
  Future<User> getProfile();

  @PUT('/auth/profile')
  Future<User> updateProfile(@Body() Map<String, dynamic> userData);

  @POST('/auth/forgot-password')
  Future<void> forgotPassword(@Body() Map<String, String> email);

  @POST('/auth/reset-password')
  Future<void> resetPassword(@Body() Map<String, String> resetData);

  @POST('/auth/verify-email')
  Future<AuthResponse> verifyEmail(@Body() Map<String, String> verificationData);
}
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../constants/app_constants.dart';
import '../providers/auth_provider.dart';
import '../../features/auth/screens/login_screen.dart';
import '../../features/auth/screens/register_screen.dart';
import '../../features/auth/screens/forgot_password_screen.dart';
import '../../features/auth/screens/email_verification_screen.dart';
import '../../features/dashboard/screens/home_screen.dart';
import '../../features/appointments/screens/appointments_screen.dart';
import '../../features/appointments/screens/book_appointment_screen.dart';
import '../../features/ai_chat/screens/ai_chat_screen.dart';
import '../../features/ai_diagnosis/screens/symptom_checker_screen.dart';
import '../../main_layout.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: authState.isAuthenticated 
        ? AppConstants.homeRoute 
        : AppConstants.loginRoute,
    redirect: (context, state) {
      final isAuthenticated = authState.isAuthenticated;
      final isLoading = authState.isLoading;
      final hasError = authState.error != null;
      final currentPath = state.uri.toString();
      final isAuthRoute = currentPath == AppConstants.loginRoute || 
                         currentPath == AppConstants.registerRoute ||
                         currentPath == AppConstants.forgotPasswordRoute ||
                         currentPath.startsWith('/email-verification');

      // Ne pas rediriger si l'état est en cours de chargement
      if (isLoading) {
        return null;
      }

      // Cela permet à l'utilisateur de corriger ses erreurs
      if (currentPath == AppConstants.registerRoute && hasError && !isAuthenticated) {
        return null;
      }

      // Si non connecté et pas sur une page d'auth, rediriger vers login
      if (!isAuthenticated && !isAuthRoute && !hasError) {
        return AppConstants.loginRoute;
      }

      // Si connecté et sur une page d'auth, rediriger vers home
      if (isAuthenticated && isAuthRoute) {
        return AppConstants.homeRoute;
      }

      return null;
    },
    routes: [
      // Routes d'authentification
      GoRoute(
        path: AppConstants.loginRoute,
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: AppConstants.registerRoute,
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: AppConstants.forgotPasswordRoute,
        builder: (context, state) => const ForgotPasswordScreen(),
      ),
      GoRoute(
        path: '/email-verification',
        builder: (context, state) {
          final token = state.uri.queryParameters['token'];
          final email = state.uri.queryParameters['email'];
          return EmailVerificationScreen(
            token: token,
            email: email,
          );
        },
      ),

      // Routes principales avec navigation
      ShellRoute(
        builder: (context, state, child) => MainLayout(child: child),
        routes: [
          GoRoute(
            path: AppConstants.homeRoute,
            builder: (context, state) => const HomeScreen(),
          ),
          GoRoute(
            path: AppConstants.appointmentsRoute,
            builder: (context, state) => const AppointmentsScreen(),
            routes: [
              GoRoute(
                path: 'book',
                builder: (context, state) => const BookAppointmentScreen(),
              ),
            ],
          ),
          GoRoute(
            path: AppConstants.chatRoute,
            builder: (context, state) => const AIChatScreen(),
          ),
          GoRoute(
            path: '/symptom-checker',
            builder: (context, state) => const SymptomCheckerScreen(),
          ),
          GoRoute(
            path: AppConstants.profileRoute,
            builder: (context, state) => const ProfileScreen(),
          ),
        ],
      ),
    ],
  );
});

// Écran de profil simple
class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    final user = authState.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profil'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              ref.read(authStateProvider.notifier).logout();
            },
          ),
        ],
      ),
      body: user == null
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  CircleAvatar(
                    radius: 50,
                    backgroundColor: Theme.of(context).primaryColor,
                    child: Text(
                      '${user.firstName[0]}${user.lastName[0]}',
                      style: const TextStyle(
                        fontSize: 32,
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    '${user.role == AppConstants.doctorRole ? 'Dr. ' : ''}${user.firstName} ${user.lastName}',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    user.email,
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: Colors.grey[600],
                    ),
                  ),
                  const SizedBox(height: 24),
                  Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Informations',
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 16),
                          _buildInfoRow('Rôle', user.role),
                          if (user.phone != null) _buildInfoRow('Téléphone', user.phone!),
                          if (user.specialty != null) _buildInfoRow('Spécialité', user.specialty!),
                          if (user.createdAt != null)
                            _buildInfoRow('Membre depuis', 
                                user.createdAt!.year.toString()),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () {
                        ref.read(authStateProvider.notifier).logout();
                      },
                      icon: const Icon(Icons.logout),
                      label: const Text('Se déconnecter'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
            ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
          Expanded(
            child: Text(value),
          ),
        ],
      ),
    );
  }
}
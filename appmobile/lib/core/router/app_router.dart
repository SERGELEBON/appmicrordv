import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../constants/app_constants.dart';
import '../providers/auth_provider.dart';
import '../theme/app_theme.dart';
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
  // Optimisation : Ne surveiller que les propriétés nécessaires pour éviter les rebuilds
  final isAuthenticated = ref.watch(authStateProvider.select((state) => state.isAuthenticated));
  final isLoading = ref.watch(authStateProvider.select((state) => state.isLoading));
  final hasError = ref.watch(authStateProvider.select((state) => state.error != null));

  return GoRouter(
    initialLocation: isAuthenticated 
        ? AppConstants.homeRoute 
        : AppConstants.loginRoute,
    redirect: (context, state) {
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

// Écran de profil professionnel
class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authStateProvider.select((state) => state.user));

    if (user == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final isDoctor = user.role == AppConstants.doctorRole;

    return Scaffold(
      backgroundColor: AppTheme.backgroundLight,
      body: RefreshIndicator(
        onRefresh: () async {
          // TODO: Rafraîchir les données utilisateur
        },
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              // En-tête du profil
              _buildProfileHeader(user, isDoctor),
              const SizedBox(height: 24),

              // Informations personnelles
              _buildInfoCard(user),
              const SizedBox(height: 16),

              // Statistiques pour les médecins
              if (isDoctor) ...[
                _buildStatsCard(),
                const SizedBox(height: 16),
              ],

              // Paramètres et actions
              _buildSettingsCard(),
              const SizedBox(height: 16),

              // Sécurité
              _buildSecurityCard(),
              
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildProfileHeader(user, bool isDoctor) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          gradient: LinearGradient(
            colors: [
              AppTheme.secondaryColor.withOpacity(0.1),
              Colors.white,
            ],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: Column(
          children: [
            Stack(
              children: [
                CircleAvatar(
                  radius: 60,
                  backgroundColor: AppTheme.secondaryColor,
                  child: Text(
                    '${user.firstName[0]}${user.lastName[0]}',
                    style: const TextStyle(
                      fontSize: 36,
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Positioned(
                  bottom: 0,
                  right: 0,
                  child: Container(
                    decoration: BoxDecoration(
                      color: AppTheme.secondaryColor,
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.white, width: 3),
                    ),
                    child: IconButton(
                      icon: const Icon(Icons.camera_alt, color: Colors.white, size: 18),
                      onPressed: () => _showEditDialog(user),
                      constraints: const BoxConstraints(minWidth: 36, minHeight: 36),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Text(
              '${isDoctor ? 'Dr. ' : ''}${user.firstName} ${user.lastName}',
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              user.email,
              style: const TextStyle(
                fontSize: 16,
                color: AppTheme.textSecondary,
              ),
            ),
            if (user.specialty != null) ...[
              const SizedBox(height: 8),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
                decoration: BoxDecoration(
                  color: AppTheme.secondaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  user.specialty!,
                  style: const TextStyle(
                    color: AppTheme.secondaryColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _buildStatItem('Membre depuis', user.createdAt?.year.toString() ?? 'N/A'),
                if (isDoctor) _buildStatItem('Patients', '127'),
                _buildStatItem('Statut', 'Actif'),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatItem(String label, String value) {
    return Column(
      children: [
        Text(
          value,
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: AppTheme.secondaryColor,
          ),
        ),
        Text(
          label,
          style: const TextStyle(
            fontSize: 12,
            color: AppTheme.textSecondary,
          ),
        ),
      ],
    );
  }

  Widget _buildInfoCard(user) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Informations personnelles',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
            ),
            const SizedBox(height: 16),
            _buildInfoRow('Prénom', user.firstName),
            _buildInfoRow('Nom', user.lastName),
            _buildInfoRow('Email', user.email),
            _buildInfoRow('Rôle', _getRoleDisplayName(user.role)),
            if (user.phone != null) _buildInfoRow('Téléphone', user.phone!),
            if (user.specialty != null) _buildInfoRow('Spécialité', user.specialty!),
          ],
        ),
      ),
    );
  }

  Widget _buildStatsCard() {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Statistiques',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(child: _buildStatCard('127', 'Patients', Icons.people, AppTheme.secondaryColor)),
                const SizedBox(width: 12),
                Expanded(child: _buildStatCard('95%', 'Satisfaction', Icons.star, AppTheme.successColor)),
                const SizedBox(width: 12),
                Expanded(child: _buildStatCard('4.8', 'Note moyenne', Icons.thumb_up, AppTheme.warningColor)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatCard(String value, String label, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(height: 8),
          Text(
            value,
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: color,
            ),
          ),
          Text(
            label,
            style: const TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildSettingsCard() {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(20),
            child: Text(
              'Paramètres',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
            ),
          ),
          _buildSettingsTile(Icons.notifications, 'Notifications', 'Gérer vos préférences', () {}),
          _buildSettingsTile(Icons.language, 'Langue', 'Français', () {}),
          _buildSettingsTile(Icons.dark_mode, 'Thème', 'Mode clair', () {}),
          _buildSettingsTile(Icons.help, 'Aide et support', 'Centre d\'aide', () {}),
        ],
      ),
    );
  }

  Widget _buildSecurityCard() {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(20),
            child: Text(
              'Sécurité et confidentialité',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
            ),
          ),
          _buildSettingsTile(Icons.lock, 'Changer le mot de passe', 'Dernière modification il y a 3 mois', () {}),
          _buildSettingsTile(Icons.security, 'Authentification à deux facteurs', 'Désactivée', () {}),
          _buildSettingsTile(Icons.privacy_tip, 'Confidentialité', 'Gérer vos données', () {}),
        ],
      ),
    );
  }

  Widget _buildSettingsTile(IconData icon, String title, String subtitle, VoidCallback onTap) {
    return ListTile(
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppTheme.secondaryColor.withOpacity(0.1),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(icon, color: AppTheme.secondaryColor, size: 20),
      ),
      title: Text(
        title,
        style: const TextStyle(
          fontWeight: FontWeight.w500,
          color: AppTheme.textPrimary,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: const TextStyle(
          color: AppTheme.textSecondary,
          fontSize: 12,
        ),
      ),
      trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: AppTheme.textSecondary),
      onTap: onTap,
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
              style: const TextStyle(
                fontWeight: FontWeight.w600,
                color: AppTheme.textSecondary,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }

  String _getRoleDisplayName(String role) {
    switch (role) {
      case 'DOCTOR':
        return 'Médecin';
      case 'PATIENT':
        return 'Patient';
      default:
        return role;
    }
  }

  void _showEditDialog(user) {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: Container(
          padding: const EdgeInsets.all(24),
          width: MediaQuery.of(context).size.width * 0.9,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: AppTheme.secondaryColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Icon(Icons.edit, color: AppTheme.secondaryColor),
                  ),
                  const SizedBox(width: 16),
                  const Text(
                    'Modifier le profil',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: AppTheme.textPrimary,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              const Text(
                'Cette fonctionnalité sera bientôt disponible.',
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 16,
                ),
              ),
              const SizedBox(height: 24),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text('Fermer'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
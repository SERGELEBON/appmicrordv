import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:go_router/go_router.dart';
import '../../../core/providers/auth_provider.dart';
import '../../../core/providers/appointment_provider.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/utils/responsive_utils.dart';
import '../widgets/quick_action_card.dart';
import '../widgets/upcoming_appointment_card.dart';
import '../widgets/health_tip_card.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  @override
  void initState() {
    super.initState();
    // Charger les données au démarrage
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(appointmentsProvider.notifier).loadAppointments();
    });
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authStateProvider);
    final appointmentsState = ref.watch(appointmentsProvider);
    final user = authState.user;

    if (user == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final isPatient = user.role == AppConstants.patientRole;
    final now = DateTime.now();
    final upcomingAppointments = appointmentsState.appointments
        .where((apt) => apt.dateTime.isAfter(now) && apt.status != 'CANCELLED')
        .toList()
      ..sort((a, b) => a.dateTime.compareTo(b.dateTime));

    return Scaffold(
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: () => ref.read(appointmentsProvider.notifier).loadAppointments(),
          child: ResponsiveUtils.buildCenteredContent(
            context: context,
            child: SingleChildScrollView(
              padding: ResponsiveUtils.getResponsivePadding(context),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                // En-tête avec salutation
                _buildHeader(user),
                SizedBox(height: ResponsiveUtils.isDesktop(context) ? AppTheme.spacingXl : AppTheme.spacingLg),

                // Actions rapides
                _buildQuickActions(isPatient),
                SizedBox(height: ResponsiveUtils.isDesktop(context) ? AppTheme.spacingXl : AppTheme.spacingLg),

                // Prochains rendez-vous
                if (upcomingAppointments.isNotEmpty) ...[
                  _buildSectionHeader('Prochains rendez-vous'),
                  const SizedBox(height: 12),
                  ...upcomingAppointments.take(3).map((appointment) =>
                      Padding(
                        padding: const EdgeInsets.only(bottom: 12),
                        child: UpcomingAppointmentCard(appointment: appointment),
                      ),
                  ),
                  if (upcomingAppointments.length > 3)
                    TextButton(
                      onPressed: () => context.push(AppConstants.appointmentsRoute),
                      child: const Text('Voir tous les rendez-vous'),
                    ),
                  const SizedBox(height: 24),
                ],

                // Conseils santé IA
                if (isPatient) ...[
                  _buildSectionHeader('Conseils santé personnalisés'),
                  const SizedBox(height: 12),
                  _buildHealthTips(),
                  const SizedBox(height: 24),
                ],

                // Statistiques pour les médecins
                if (!isPatient) ...[
                  _buildSectionHeader('Aperçu de la journée'),
                  const SizedBox(height: 12),
                  _buildDoctorStats(appointmentsState.appointments),
                  const SizedBox(height: 24),
                ],

                // Urgences et alertes
                _buildEmergencyCard(),
              ],
            ),
          ),
        ),
      ),
      )
    );
  }

  Widget _buildHeader(user) {
    final greeting = _getGreeting();
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.accentColor],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            greeting,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: Colors.white.withOpacity(0.9),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '${user.role == AppConstants.doctorRole ? 'Dr. ' : ''}${user.firstName} ${user.lastName}',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            DateFormat('EEEE d MMMM yyyy', 'fr_FR').format(DateTime.now()),
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: Colors.white.withOpacity(0.8),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickActions(bool isPatient) {
    final actions = _getQuickActions(isPatient);
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader('Actions rapides'),
        SizedBox(height: AppTheme.spacingMd),
        ResponsiveUtils.buildResponsiveGrid(
          context: context,
          children: actions,
          childAspectRatio: ResponsiveUtils.isTablet(context) || ResponsiveUtils.isDesktop(context) ? 1.3 : 1.1,
          mainAxisSpacing: AppTheme.spacingMd,
          crossAxisSpacing: AppTheme.spacingMd,
        ),
      ],
    );
  }

  List<Widget> _getQuickActions(bool isPatient) {
    if (isPatient) {
      return [
        QuickActionCard(
          title: 'Prendre RDV',
          subtitle: 'Nouveau rendez-vous',
          icon: Icons.calendar_today,
          color: AppTheme.primaryColor,
          onTap: () => context.push('${AppConstants.appointmentsRoute}/book'),
        ),
        QuickActionCard(
          title: 'Assistant IA',
          subtitle: 'Symptômes & conseils',
          icon: Icons.smart_toy,
          color: AppTheme.secondaryColor,
          onTap: () => context.push(AppConstants.chatRoute),
        ),
        QuickActionCard(
          title: 'Mes RDV',
          subtitle: 'Voir l\'historique',
          icon: Icons.history,
          color: AppTheme.accentColor,
          onTap: () => context.push(AppConstants.appointmentsRoute),
        ),
        QuickActionCard(
          title: 'Urgence',
          subtitle: 'Aide immédiate',
          icon: Icons.emergency,
          color: AppTheme.errorColor,
          onTap: () => _showEmergencyDialog(),
        ),
      ];
    } else {
      return [
        QuickActionCard(
          title: 'Mes Patients',
          subtitle: 'Planning du jour',
          icon: Icons.people,
          color: AppTheme.primaryColor,
          onTap: () => context.push(AppConstants.appointmentsRoute),
        ),
        QuickActionCard(
          title: 'IA Résumé',
          subtitle: 'Notes consultation',
          icon: Icons.psychology,
          color: AppTheme.secondaryColor,
          onTap: () => context.push('${AppConstants.chatRoute}/consultation'),
        ),
        QuickActionCard(
          title: 'Urgences',
          subtitle: 'Priorisation IA',
          icon: Icons.priority_high,
          color: AppTheme.warningColor,
          onTap: () => context.push('${AppConstants.appointmentsRoute}/urgencies'),
        ),
        QuickActionCard(
          title: 'Statistiques',
          subtitle: 'Analyses IA',
          icon: Icons.analytics,
          color: AppTheme.accentColor,
          onTap: () => context.push('${AppConstants.profileRoute}/stats'),
        ),
      ];
    }
  }

  Widget _buildHealthTips() {
    // Conseils statiques pour la démonstration
    final tips = [
      {
        'title': 'Hydratation',
        'content': 'Buvez au moins 1.5L d\'eau par jour pour maintenir une bonne hydratation.',
        'icon': Icons.water_drop,
        'color': AppTheme.accentColor,
      },
      {
        'title': 'Exercice',
        'content': '30 minutes d\'activité physique par jour réduisent les risques cardiovasculaires.',
        'icon': Icons.fitness_center,
        'color': AppTheme.successColor,
      },
      {
        'title': 'Sommeil',
        'content': '7-8h de sommeil par nuit améliorent votre système immunitaire.',
        'icon': Icons.bedtime,
        'color': AppTheme.primaryColor,
      },
    ];

    return SizedBox(
      height: 140,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: tips.length,
        itemBuilder: (context, index) {
          final tip = tips[index];
          return Container(
            width: 280,
            margin: EdgeInsets.only(right: index < tips.length - 1 ? 12 : 0),
            child: HealthTipCard(
              title: tip['title'] as String,
              content: tip['content'] as String,
              icon: tip['icon'] as IconData,
              color: tip['color'] as Color,
            ),
          );
        },
      ),
    );
  }

  Widget _buildDoctorStats(List appointments) {
    final today = DateTime.now();
    final todayAppointments = appointments.where((apt) =>
        apt.dateTime.day == today.day &&
        apt.dateTime.month == today.month &&
        apt.dateTime.year == today.year).toList();

    return Row(
      children: [
        Expanded(
          child: _buildStatCard(
            'Aujourd\'hui',
            '${todayAppointments.length}',
            Icons.today,
            AppTheme.primaryColor,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildStatCard(
            'Cette semaine',
            '${appointments.length}',
            Icons.date_range,
            AppTheme.accentColor,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildStatCard(
            'Urgences',
            '2', // Statique pour la démo
            Icons.priority_high,
            AppTheme.errorColor,
          ),
        ),
      ],
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Icon(icon, color: color, size: 32),
            const SizedBox(height: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            Text(
              title,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: AppTheme.textSecondary,
              ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmergencyCard() {
    return Card(
      color: AppTheme.errorColor.withOpacity(0.1),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(
              Icons.emergency,
              color: AppTheme.errorColor,
              size: 32,
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Urgence médicale',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: AppTheme.errorColor,
                    ),
                  ),
                  Text(
                    'En cas d\'urgence, appelez le 15 (SAMU)',
                    style: Theme.of(context).textTheme.bodyMedium,
                  ),
                ],
              ),
            ),
            ElevatedButton(
              onPressed: () => _showEmergencyDialog(),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.errorColor,
              ),
              child: const Text('Appeler'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(
      title,
      style: Theme.of(context).textTheme.titleLarge?.copyWith(
        fontWeight: FontWeight.bold,
      ),
    );
  }

  String _getGreeting() {
    final hour = DateTime.now().hour;
    if (hour < 12) return 'Bonjour';
    if (hour < 18) return 'Bon après-midi';
    return 'Bonsoir';
  }

  void _showEmergencyDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            Icon(Icons.emergency, color: AppTheme.errorColor),
            const SizedBox(width: 8),
            const Text('Urgence médicale'),
          ],
        ),
        content: const Text(
          'En cas d\'urgence vitale, composez immédiatement le 15 (SAMU) ou le 112.\n\nPour une urgence non vitale, vous pouvez vous rendre aux urgences de l\'hôpital le plus proche.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer'),
          ),
          ElevatedButton(
            onPressed: () {
              // TODO: Implémenter l'appel d'urgence
              Navigator.pop(context);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppTheme.errorColor,
            ),
            child: const Text('Appeler 15'),
          ),
        ],
      ),
    );
  }
}
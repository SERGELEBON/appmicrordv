import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../core/providers/appointment_provider.dart';
import '../../../core/providers/auth_provider.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../widgets/appointment_card.dart';
import 'book_appointment_screen.dart';

class AppointmentsScreen extends ConsumerStatefulWidget {
  const AppointmentsScreen({super.key});

  @override
  ConsumerState<AppointmentsScreen> createState() => _AppointmentsScreenState();
}

class _AppointmentsScreenState extends ConsumerState<AppointmentsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    
    // Charger les rendez-vous
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(appointmentsProvider.notifier).loadAppointments();
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final appointmentsState = ref.watch(appointmentsProvider);
    final authState = ref.watch(authStateProvider);
    
    ref.listen(appointmentsProvider, (previous, next) {
      if (next.error != null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.error!),
            backgroundColor: AppTheme.errorColor,
          ),
        );
        ref.read(appointmentsProvider.notifier).clearError();
      }
    });

    final now = DateTime.now();
    final upcomingAppointments = appointmentsState.appointments
        .where((apt) => apt.dateTime.isAfter(now) && apt.status != 'CANCELLED')
        .toList()
      ..sort((a, b) => a.dateTime.compareTo(b.dateTime));

    final pastAppointments = appointmentsState.appointments
        .where((apt) => apt.dateTime.isBefore(now) || apt.status == 'COMPLETED')
        .toList()
      ..sort((a, b) => b.dateTime.compareTo(a.dateTime));

    final cancelledAppointments = appointmentsState.appointments
        .where((apt) => apt.status == 'CANCELLED')
        .toList()
      ..sort((a, b) => b.dateTime.compareTo(a.dateTime));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mes Rendez-vous'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(text: 'À venir'),
            Tab(text: 'Passés'),
            Tab(text: 'Annulés'),
          ],
        ),
      ),
      body: appointmentsState.isLoading
          ? const Center(child: CircularProgressIndicator())
          : TabBarView(
              controller: _tabController,
              children: [
                // Rendez-vous à venir
                _buildAppointmentsList(upcomingAppointments, 'upcoming'),
                // Rendez-vous passés
                _buildAppointmentsList(pastAppointments, 'past'),
                // Rendez-vous annulés
                _buildAppointmentsList(cancelledAppointments, 'cancelled'),
              ],
            ),
      floatingActionButton: authState.user?.role == AppConstants.patientRole
          ? FloatingActionButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const BookAppointmentScreen(),
                  ),
                );
              },
              child: const Icon(Icons.add),
            )
          : null,
    );
  }

  Widget _buildAppointmentsList(List appointments, String type) {
    if (appointments.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              type == 'upcoming'
                  ? Icons.calendar_today
                  : type == 'past'
                      ? Icons.history
                      : Icons.cancel,
              size: 64,
              color: AppTheme.textSecondary,
            ),
            const SizedBox(height: 16),
            Text(
              type == 'upcoming'
                  ? 'Aucun rendez-vous à venir'
                  : type == 'past'
                      ? 'Aucun rendez-vous passé'
                      : 'Aucun rendez-vous annulé',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
            ),
            if (type == 'upcoming') ...[
              const SizedBox(height: 8),
              Text(
                'Appuyez sur + pour prendre un rendez-vous',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
              ),
            ],
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => ref.read(appointmentsProvider.notifier).loadAppointments(),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: appointments.length,
        itemBuilder: (context, index) {
          final appointment = appointments[index];
          return Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: AppointmentCard(
              appointment: appointment,
              type: type,
              onCancel: type == 'upcoming'
                  ? () => _cancelAppointment(appointment.id)
                  : null,
              onReschedule: type == 'upcoming'
                  ? () => _rescheduleAppointment(appointment)
                  : null,
            ),
          );
        },
      ),
    );
  }

  Future<void> _cancelAppointment(int appointmentId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Annuler le rendez-vous'),
        content: const Text(
          'Êtes-vous sûr de vouloir annuler ce rendez-vous ? Cette action est irréversible.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Non'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: AppTheme.errorColor),
            child: const Text('Oui, annuler'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      final success = await ref
          .read(appointmentsProvider.notifier)
          .cancelAppointment(appointmentId);
      
      if (success && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Rendez-vous annulé avec succès'),
            backgroundColor: AppTheme.successColor,
          ),
        );
      }
    }
  }

  Future<void> _rescheduleAppointment(appointment) async {
    // TODO: Implémenter la reprogrammation
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Fonction de reprogrammation à implémenter'),
      ),
    );
  }
}
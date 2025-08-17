import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../core/models/appointment.dart';
import '../../../core/theme/app_theme.dart';

class AppointmentCard extends StatelessWidget {
  final Appointment appointment;
  final String type;
  final VoidCallback? onCancel;
  final VoidCallback? onReschedule;

  const AppointmentCard({
    super.key,
    required this.appointment,
    required this.type,
    this.onCancel,
    this.onReschedule,
  });

  @override
  Widget build(BuildContext context) {
    final dateFormat = DateFormat('dd/MM/yyyy');
    final timeFormat = DateFormat('HH:mm');
    
    Color statusColor;
    IconData statusIcon;
    String statusText;

    switch (appointment.status) {
      case 'CONFIRMED':
        statusColor = AppTheme.successColor;
        statusIcon = Icons.check_circle;
        statusText = 'Confirmé';
        break;
      case 'PENDING':
        statusColor = AppTheme.warningColor;
        statusIcon = Icons.schedule;
        statusText = 'En attente';
        break;
      case 'CANCELLED':
        statusColor = AppTheme.errorColor;
        statusIcon = Icons.cancel;
        statusText = 'Annulé';
        break;
      case 'COMPLETED':
        statusColor = AppTheme.primaryColor;
        statusIcon = Icons.done_all;
        statusText = 'Terminé';
        break;
      default:
        statusColor = AppTheme.textSecondary;
        statusIcon = Icons.help;
        statusText = appointment.status;
    }

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // En-tête avec statut
            Row(
              children: [
                Icon(
                  statusIcon,
                  color: statusColor,
                  size: 20,
                ),
                const SizedBox(width: 8),
                Text(
                  statusText,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: statusColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppTheme.primaryColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    appointment.doctor.specialty ?? 'Généraliste',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.primaryColor,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            
            // Informations du médecin
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: AppTheme.primaryColor,
                  child: Text(
                    '${appointment.doctor.firstName[0]}${appointment.doctor.lastName[0]}',
                    style: const TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      if (appointment.doctor.specialty != null)
                        Text(
                          appointment.doctor.specialty!,
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // Date et heure
            Row(
              children: [
                Icon(
                  Icons.calendar_today,
                  size: 16,
                  color: AppTheme.textSecondary,
                ),
                const SizedBox(width: 8),
                Text(
                  dateFormat.format(appointment.dateTime),
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
                const SizedBox(width: 16),
                Icon(
                  Icons.access_time,
                  size: 16,
                  color: AppTheme.textSecondary,
                ),
                const SizedBox(width: 8),
                Text(
                  timeFormat.format(appointment.dateTime),
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ],
            ),
            
            // Notes si présentes
            if (appointment.notes != null && appointment.notes!.isNotEmpty) ...[
              const SizedBox(height: 12),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppTheme.backgroundLight,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Notes',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        fontWeight: FontWeight.w600,
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      appointment.notes!,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ],
                ),
              ),
            ],
            
            // Actions pour les rendez-vous à venir
            if (type == 'upcoming' && appointment.status != 'CANCELLED') ...[
              const SizedBox(height: 16),
              Row(
                children: [
                  if (onReschedule != null)
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: onReschedule,
                        icon: const Icon(Icons.schedule),
                        label: const Text('Reprogrammer'),
                      ),
                    ),
                  if (onReschedule != null && onCancel != null)
                    const SizedBox(width: 12),
                  if (onCancel != null)
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: onCancel,
                        icon: const Icon(Icons.cancel),
                        label: const Text('Annuler'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: AppTheme.errorColor,
                          side: const BorderSide(color: AppTheme.errorColor),
                        ),
                      ),
                    ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}
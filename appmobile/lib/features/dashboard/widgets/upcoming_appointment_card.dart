import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../core/models/appointment.dart';
import '../../../core/theme/app_theme.dart';

class UpcomingAppointmentCard extends StatelessWidget {
  final Appointment appointment;

  const UpcomingAppointmentCard({
    super.key,
    required this.appointment,
  });

  @override
  Widget build(BuildContext context) {
    final dateFormat = DateFormat('dd/MM');
    final timeFormat = DateFormat('HH:mm');
    final dayFormat = DateFormat('EEEE', 'fr_FR');
    
    final isToday = DateTime.now().day == appointment.dateTime.day &&
        DateTime.now().month == appointment.dateTime.month &&
        DateTime.now().year == appointment.dateTime.year;

    return Card(
      elevation: 2,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          border: isToday
              ? Border.all(color: AppTheme.primaryColor, width: 2)
              : null,
        ),
        child: Row(
          children: [
            // Date
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                color: isToday
                    ? AppTheme.primaryColor
                    : AppTheme.primaryColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    dateFormat.format(appointment.dateTime).split('/')[0],
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: isToday ? Colors.white : AppTheme.primaryColor,
                    ),
                  ),
                  Text(
                    dateFormat.format(appointment.dateTime).split('/')[1],
                    style: TextStyle(
                      fontSize: 12,
                      color: isToday
                          ? Colors.white.withOpacity(0.8)
                          : AppTheme.primaryColor.withOpacity(0.7),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 16),
            
            // Informations
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
                  const SizedBox(height: 4),
                  if (appointment.doctor.specialty != null)
                    Text(
                      appointment.doctor.specialty!,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Icon(
                        Icons.access_time,
                        size: 16,
                        color: AppTheme.textSecondary,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        timeFormat.format(appointment.dateTime),
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(width: 12),
                      if (isToday) ...[
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: AppTheme.successColor.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            'Aujourd\'hui',
                            style: TextStyle(
                              fontSize: 12,
                              color: AppTheme.successColor,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ] else ...[
                        Text(
                          dayFormat.format(appointment.dateTime),
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: AppTheme.textSecondary,
                            fontStyle: FontStyle.italic,
                          ),
                        ),
                      ],
                    ],
                  ),
                ],
              ),
            ),
            
            // Statut
            Icon(
              appointment.status == 'CONFIRMED'
                  ? Icons.check_circle
                  : Icons.schedule,
              color: appointment.status == 'CONFIRMED'
                  ? AppTheme.successColor
                  : AppTheme.warningColor,
            ),
          ],
        ),
      ),
    );
  }
}
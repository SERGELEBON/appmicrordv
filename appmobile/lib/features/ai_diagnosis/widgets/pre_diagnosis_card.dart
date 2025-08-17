import 'package:flutter/material.dart';
import '../../../core/models/chat.dart';
import '../../../core/theme/app_theme.dart';

class PreDiagnosisCard extends StatelessWidget {
  final AIPreDiagnosis preDiagnosis;
  final Function(String) onBookAppointment;
  final VoidCallback onMoreInfo;

  const PreDiagnosisCard({
    super.key,
    required this.preDiagnosis,
    required this.onBookAppointment,
    required this.onMoreInfo,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // En-tête avec niveau d'urgence
            Row(
              children: [
                Icon(
                  Icons.psychology,
                  color: AppTheme.primaryColor,
                  size: 24,
                ),
                const SizedBox(width: 8),
                Text(
                  'Pré-diagnostic IA',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const Spacer(),
                _buildUrgencyBadge(preDiagnosis.urgencyLevel),
              ],
            ),
            const SizedBox(height: 16),

            // Score de confiance
            if (preDiagnosis.confidenceScore != null) ...[
              Row(
                children: [
                  Text(
                    'Confiance: ',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  Text('${(preDiagnosis.confidenceScore! * 100).round()}%'),
                  const SizedBox(width: 8),
                  Expanded(
                    child: LinearProgressIndicator(
                      value: preDiagnosis.confidenceScore!,
                      backgroundColor: Colors.grey[300],
                      valueColor: AlwaysStoppedAnimation<Color>(
                        _getConfidenceColor(preDiagnosis.confidenceScore!),
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
            ],

            // Spécialité recommandée
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppTheme.primaryColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(
                        Icons.medical_services,
                        color: AppTheme.primaryColor,
                        size: 20,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        'Spécialiste recommandé',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                          color: AppTheme.primaryColor,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    preDiagnosis.suggestedSpecialty,
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Causes possibles
            _buildSection(
              context,
              'Causes possibles',
              Icons.search,
              preDiagnosis.possibleCauses,
            ),
            const SizedBox(height: 16),

            // Tests recommandés
            _buildSection(
              context,
              'Examens recommandés',
              Icons.assignment,
              preDiagnosis.recommendedTests,
            ),
            const SizedBox(height: 16),

            // Disclaimer
            if (preDiagnosis.disclaimer != null) ...[
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppTheme.warningColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    color: AppTheme.warningColor.withOpacity(0.3),
                  ),
                ),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Icon(
                      Icons.warning_amber,
                      color: AppTheme.warningColor,
                      size: 20,
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        preDiagnosis.disclaimer!,
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: AppTheme.warningColor,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
            ],

            // Actions
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: onMoreInfo,
                    icon: const Icon(Icons.info_outline),
                    label: const Text('Plus d\'infos'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => onBookAppointment(preDiagnosis.suggestedSpecialty),
                    icon: const Icon(Icons.calendar_today),
                    label: const Text('Prendre RDV'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUrgencyBadge(String urgencyLevel) {
    Color color;
    IconData icon;
    String text;

    switch (urgencyLevel.toLowerCase()) {
      case 'urgent':
        color = AppTheme.errorColor;
        icon = Icons.emergency;
        text = 'URGENT';
        break;
      case 'high':
        color = AppTheme.warningColor;
        icon = Icons.priority_high;
        text = 'ÉLEVÉ';
        break;
      case 'medium':
        color = AppTheme.accentColor;
        icon = Icons.schedule;
        text = 'MODÉRÉ';
        break;
      case 'low':
      default:
        color = AppTheme.successColor;
        icon = Icons.check_circle;
        text = 'FAIBLE';
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, color: color, size: 16),
          const SizedBox(width: 4),
          Text(
            text,
            style: TextStyle(
              color: color,
              fontWeight: FontWeight.w600,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSection(
    BuildContext context,
    String title,
    IconData icon,
    List<String> items,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(icon, size: 20, color: AppTheme.textSecondary),
            const SizedBox(width: 8),
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ...items.map((item) => Padding(
              padding: const EdgeInsets.only(left: 28, bottom: 4),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 4,
                    height: 4,
                    margin: const EdgeInsets.only(top: 8),
                    decoration: const BoxDecoration(
                      color: AppTheme.primaryColor,
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      item,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                ],
              ),
            )),
      ],
    );
  }

  Color _getConfidenceColor(double confidence) {
    if (confidence >= 0.8) return AppTheme.successColor;
    if (confidence >= 0.6) return AppTheme.accentColor;
    if (confidence >= 0.4) return AppTheme.warningColor;
    return AppTheme.errorColor;
  }
}
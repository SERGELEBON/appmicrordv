import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/providers/chat_provider.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../widgets/symptom_selector.dart';
import '../widgets/pre_diagnosis_card.dart';

class SymptomCheckerScreen extends ConsumerStatefulWidget {
  const SymptomCheckerScreen({super.key});

  @override
  ConsumerState<SymptomCheckerScreen> createState() => _SymptomCheckerScreenState();
}

class _SymptomCheckerScreenState extends ConsumerState<SymptomCheckerScreen> {
  final List<String> _commonSymptoms = [
    'Fièvre',
    'Mal de tête',
    'Mal de gorge',
    'Toux',
    'Douleur abdominale',
    'Nausées',
    'Fatigue',
    'Douleur thoracique',
    'Essoufflement',
    'Vertiges',
    'Douleur articulaire',
    'Éruption cutanée',
    'Diarrhée',
    'Constipation',
    'Perte d\'appétit',
    'Insomnie',
    'Anxiété',
    'Palpitations',
    'Vision floue',
    'Acouphènes',
  ];

  @override
  Widget build(BuildContext context) {
    final diagnosisState = ref.watch(aiDiagnosisProvider);

    ref.listen(aiDiagnosisProvider, (previous, next) {
      if (next.error != null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.error!),
            backgroundColor: AppTheme.errorColor,
          ),
        );
        ref.read(aiDiagnosisProvider.notifier).clearError();
      }
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Vérificateur de Symptômes'),
        actions: [
          if (diagnosisState.currentSymptoms.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.clear_all),
              onPressed: () {
                ref.read(aiDiagnosisProvider.notifier).clearSymptoms();
              },
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // En-tête informatif
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppTheme.primaryColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                children: [
                  Icon(
                    Icons.health_and_safety,
                    size: 48,
                    color: AppTheme.primaryColor,
                  ),
                  const SizedBox(height: 12),
                  Text(
                    'Analyseur de Symptômes IA',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Sélectionnez vos symptômes pour obtenir un pré-diagnostic et des recommandations.',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: AppTheme.warningColor.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Text(
                      'Ne remplace pas un avis médical professionnel',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.warningColor,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Symptômes sélectionnés
            if (diagnosisState.currentSymptoms.isNotEmpty) ...[
              Text(
                'Symptômes sélectionnés (${diagnosisState.currentSymptoms.length})',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: diagnosisState.currentSymptoms.map((symptom) {
                  return Chip(
                    label: Text(symptom),
                    deleteIcon: const Icon(Icons.close, size: 16),
                    onDeleted: () {
                      ref.read(aiDiagnosisProvider.notifier).removeSymptom(symptom);
                    },
                    backgroundColor: AppTheme.primaryColor.withOpacity(0.1),
                  );
                }).toList(),
              ),
              const SizedBox(height: 24),
            ],

            // Sélecteur de symptômes
            Text(
              'Sélectionnez vos symptômes',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            SymptomSelector(
              symptoms: _commonSymptoms,
              selectedSymptoms: diagnosisState.currentSymptoms,
              onSymptomTap: (symptom) {
                if (diagnosisState.currentSymptoms.contains(symptom)) {
                  ref.read(aiDiagnosisProvider.notifier).removeSymptom(symptom);
                } else {
                  ref.read(aiDiagnosisProvider.notifier).addSymptom(symptom);
                }
              },
            ),
            const SizedBox(height: 24),

            // Bouton d'analyse
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton.icon(
                onPressed: diagnosisState.currentSymptoms.isNotEmpty && !diagnosisState.isLoading
                    ? () {
                        ref.read(aiDiagnosisProvider.notifier).analyzeSymptoms();
                      }
                    : null,
                icon: diagnosisState.isLoading
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      )
                    : const Icon(Icons.psychology),
                label: Text(diagnosisState.isLoading ? 'Analyse en cours...' : 'Analyser les symptômes'),
              ),
            ),
            const SizedBox(height: 24),

            // Résultat du pré-diagnostic
            if (diagnosisState.preDiagnosis != null) ...[
              PreDiagnosisCard(
                preDiagnosis: diagnosisState.preDiagnosis!,
                onBookAppointment: (specialty) {
                  context.push('${AppConstants.appointmentsRoute}/book?specialty=$specialty');
                },
                onMoreInfo: () {
                  _showDetailedQuestionnaire();
                },
              ),
            ],
          ],
        ),
      ),
    );
  }

  void _showDetailedQuestionnaire() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.7,
        maxChildSize: 0.95,
        minChildSize: 0.5,
        builder: (context, scrollController) {
          return Container(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
                const SizedBox(height: 16),
                Text(
                  'Questionnaire détaillé',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                Expanded(
                  child: ListView(
                    controller: scrollController,
                    children: [
                      Text(
                        'Pour un diagnostic plus précis, répondez à ces questions supplémentaires:',
                        style: Theme.of(context).textTheme.bodyMedium,
                      ),
                      const SizedBox(height: 16),
                      // TODO: Implémenter le questionnaire détaillé
                      Card(
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            children: [
                              Icon(
                                Icons.construction,
                                size: 48,
                                color: AppTheme.textSecondary,
                              ),
                              const SizedBox(height: 16),
                              Text(
                                'Questionnaire détaillé en cours de développement',
                                style: Theme.of(context).textTheme.titleMedium,
                                textAlign: TextAlign.center,
                              ),
                              const SizedBox(height: 8),
                              Text(
                                'Cette fonctionnalité sera bientôt disponible pour un diagnostic plus précis.',
                                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                  color: AppTheme.textSecondary,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
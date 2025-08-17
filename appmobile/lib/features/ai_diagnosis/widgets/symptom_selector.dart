import 'package:flutter/material.dart';
import '../../../core/theme/app_theme.dart';

class SymptomSelector extends StatelessWidget {
  final List<String> symptoms;
  final List<String> selectedSymptoms;
  final Function(String) onSymptomTap;

  const SymptomSelector({
    super.key,
    required this.symptoms,
    required this.selectedSymptoms,
    required this.onSymptomTap,
  });

  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: symptoms.map((symptom) {
        final isSelected = selectedSymptoms.contains(symptom);
        return FilterChip(
          label: Text(symptom),
          selected: isSelected,
          onSelected: (selected) => onSymptomTap(symptom),
          selectedColor: AppTheme.primaryColor.withOpacity(0.2),
          checkmarkColor: AppTheme.primaryColor,
          backgroundColor: Colors.grey[100],
          labelStyle: TextStyle(
            color: isSelected ? AppTheme.primaryColor : AppTheme.textPrimary,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
          ),
        );
      }).toList(),
    );
  }
}
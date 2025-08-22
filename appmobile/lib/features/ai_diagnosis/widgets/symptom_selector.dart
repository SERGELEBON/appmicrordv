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
      spacing: 12,
      runSpacing: 12,
      children: symptoms.map((symptom) {
        final isSelected = selectedSymptoms.contains(symptom);
        return InkWell(
          onTap: () => onSymptomTap(symptom),
          borderRadius: BorderRadius.circular(25),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
            decoration: BoxDecoration(
              color: isSelected 
                  ? AppTheme.secondaryColor
                  : Colors.white,
              borderRadius: BorderRadius.circular(25),
              border: Border.all(
                color: isSelected 
                    ? AppTheme.secondaryColor
                    : Colors.grey.shade300,
                width: 1.5,
              ),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 4,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                if (isSelected) ...[
                  Icon(
                    Icons.check_circle,
                    size: 16,
                    color: Colors.white,
                  ),
                  const SizedBox(width: 6),
                ],
                Text(
                  symptom,
                  style: TextStyle(
                    color: isSelected ? Colors.white : AppTheme.textPrimary,
                    fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }
}
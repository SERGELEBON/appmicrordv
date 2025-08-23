import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../../core/models/chat.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/chat_provider.dart';
import '../../../core/utils/responsive_utils.dart';

class ChatMessageWidget extends ConsumerWidget {
  final ChatMessage message;
  final Function(String)? onSpecialtyTap;
  final VoidCallback? onBookAppointment;

  const ChatMessageWidget({
    super.key,
    required this.message,
    this.onSpecialtyTap,
    this.onBookAppointment,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isUser = message.role == 'user';
    final timeFormat = DateFormat('HH:mm');

    return Row(
      mainAxisAlignment: isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (!isUser) ...[
          CircleAvatar(
            backgroundColor: AppTheme.primaryColor,
            radius: 16,
            child: Icon(
              message.type == 'suggestion' ? Icons.lightbulb : Icons.smart_toy,
              color: Colors.white,
              size: 16,
            ),
          ),
          const SizedBox(width: 8),
        ],
        
        Flexible(
          child: ConstrainedBox(
            constraints: BoxConstraints(
              maxWidth: ResponsiveUtils.getScreenWidth(context) * 
                (ResponsiveUtils.isDesktop(context) ? 0.6 : 0.75),
              minWidth: 120,
            ),
            child: Container(
              padding: EdgeInsets.all(ResponsiveUtils.isDesktop(context) ? AppTheme.spacingMd : AppTheme.spacingMd),
              decoration: BoxDecoration(
                color: isUser ? AppTheme.primaryColor : Colors.grey[100],
                borderRadius: BorderRadius.circular(AppTheme.radiusLg).copyWith(
                  bottomLeft: isUser ? Radius.circular(AppTheme.radiusLg) : Radius.circular(AppTheme.spacingXs),
                  bottomRight: isUser ? Radius.circular(AppTheme.spacingXs) : Radius.circular(AppTheme.radiusLg),
                ),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Contenu du message
                  Text(
                    message.content,
                    style: TextStyle(
                      color: isUser ? Colors.white : AppTheme.textPrimary,
                      fontSize: 16,
                    ),
                  ),
                  
                  // Actions spéciales pour les suggestions IA
                  if (!isUser && message.type == 'suggestion') ...[
                    const SizedBox(height: 12),
                    _buildSuggestionActions(context, ref),
                  ],
                  
                  // Heure
                  const SizedBox(height: 4),
                  Text(
                    timeFormat.format(message.timestamp),
                    style: TextStyle(
                      color: isUser 
                          ? Colors.white.withOpacity(0.7) 
                          : AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
        
        if (isUser) ...[
          const SizedBox(width: 8),
          CircleAvatar(
            backgroundColor: AppTheme.accentColor,
            radius: 16,
            child: const Icon(
              Icons.person,
              color: Colors.white,
              size: 16,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildSuggestionActions(BuildContext context, WidgetRef ref) {
    final suggestedSpecialty = message.metadata?['suggestedSpecialty'] as String?;
    
    return Column(
      children: [
        const Divider(color: AppTheme.textSecondary),
        Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () {
                  if (suggestedSpecialty != null && onSpecialtyTap != null) {
                    onSpecialtyTap!(suggestedSpecialty);
                  }
                },
                icon: const Icon(Icons.calendar_today, size: 16),
                label: const Text(
                  'Prendre RDV',
                  overflow: TextOverflow.ellipsis,
                ),
                style: OutlinedButton.styleFrom(
                  foregroundColor: AppTheme.primaryColor,
                  side: const BorderSide(color: AppTheme.primaryColor),
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                ),
              ),
            ),
            const SizedBox(width: 8),
            Container(
              constraints: const BoxConstraints(minWidth: 40, minHeight: 40),
              child: IconButton(
                onPressed: () {
                  ref.read(speechProvider.notifier).speak(message.content);
                },
                icon: const Icon(Icons.volume_up, size: 16),
                style: IconButton.styleFrom(
                  foregroundColor: AppTheme.primaryColor,
                  padding: const EdgeInsets.all(8),
                ),
              ),
            ),
          ],
        ),
        
        // Affichage de la spécialité recommandée
        if (suggestedSpecialty != null) ...[
          const SizedBox(height: 8),
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: AppTheme.primaryColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Row(
              children: [
                Icon(
                  Icons.medical_services,
                  size: 16,
                  color: AppTheme.primaryColor,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Spécialité recommandée: $suggestedSpecialty',
                    style: const TextStyle(
                      color: AppTheme.primaryColor,
                      fontWeight: FontWeight.w500,
                      fontSize: 12,
                    ),
                    overflow: TextOverflow.ellipsis,
                    maxLines: 2,
                  ),
                ),
              ],
            ),
          ),
        ],
      ],
    );
  }
}
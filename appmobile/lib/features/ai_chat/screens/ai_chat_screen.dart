import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/providers/chat_provider.dart';
import '../../../core/providers/appointment_provider.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/utils/responsive_utils.dart';
import '../widgets/chat_message_widget.dart';
import '../widgets/voice_input_widget.dart';

class AIChatScreen extends ConsumerStatefulWidget {
  const AIChatScreen({super.key});

  @override
  ConsumerState<AIChatScreen> createState() => _AIChatScreenState();
}

class _AIChatScreenState extends ConsumerState<AIChatScreen> {
  final _messageController = TextEditingController();
  final _scrollController = ScrollController();

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final chatState = ref.watch(chatProvider);
    final speechState = ref.watch(speechProvider);

    ref.listen(chatProvider, (previous, next) {
      if (next.messages.length > (previous?.messages.length ?? 0)) {
        _scrollToBottom();
      }
      
      if (next.error != null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.error!),
            backgroundColor: AppTheme.errorColor,
          ),
        );
        ref.read(chatProvider.notifier).clearError();
      }
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Assistant Médical IA'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              ref.read(chatProvider.notifier).startNewConversation();
            },
          ),
        ],
      ),
      body: ResponsiveUtils.buildCenteredContent(
        context: context,
        child: Column(
          children: [
            // Messages de bienvenue si conversation vide
            if (chatState.messages.isEmpty) 
              Expanded(
                child: SingleChildScrollView(
                  child: _buildWelcomeCard(),
                ),
              ),
            
            // Liste des messages optimisée
            if (chatState.messages.isNotEmpty)
              Expanded(
                child: ListView.builder(
                  controller: _scrollController,
                  padding: ResponsiveUtils.getResponsivePadding(context),
                  itemCount: chatState.messages.length,
                  // Optimisations de performance critiques
                  addAutomaticKeepAlives: false, // Évite de garder les widgets en mémoire
                  addRepaintBoundaries: false,   // Évite les repaint boundaries automatiques
                  cacheExtent: 200,             // Limite le cache à 200px pour économiser la mémoire
                  itemBuilder: (context, index) {
                    final message = chatState.messages[index];
                    return RepaintBoundary(
                      key: ValueKey(message.id), // Clé stable pour les optimisations
                      child: Padding(
                        padding: EdgeInsets.only(bottom: AppTheme.spacingMd),
                        child: ChatMessageWidget(
                          message: message,
                          onSpecialtyTap: (specialty) => _handleSpecialtyTap(specialty),
                          onBookAppointment: () => _navigateToBookAppointment(),
                        ),
                      ),
                    );
                  },
                ),
              ),

            // Indicateur de frappe
            if (chatState.isLoading)
              Container(
                padding: ResponsiveUtils.getResponsivePadding(context),
                child: Row(
                  children: [
                    CircleAvatar(
                      backgroundColor: AppTheme.primaryColor,
                      radius: 16,
                      child: const Icon(
                        Icons.smart_toy,
                        color: Colors.white,
                        size: 16,
                      ),
                    ),
                    SizedBox(width: AppTheme.spacingMd),
                    const Text('L\'IA réfléchit...'),
                    SizedBox(width: AppTheme.spacingSm),
                    const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                  ],
                ),
              ),

            // Zone de saisie professionnelle
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Theme.of(context).scaffoldBackgroundColor,
                border: Border(
                  top: BorderSide(
                    color: AppTheme.textSecondary.withOpacity(0.2),
                    width: 1,
                  ),
                ),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.08),
                    blurRadius: 12,
                    offset: const Offset(0, -4),
                  ),
                ],
              ),
              child: SafeArea(
                child: _buildInputSection(),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInputSection() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // Zone de saisie principale
        Container(
          width: double.infinity,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(24),
            border: Border.all(
              color: AppTheme.textSecondary.withOpacity(0.3),
              width: 1,
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 8,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Row(
            children: [
              // Bouton microphone
              Padding(
                padding: const EdgeInsets.only(left: 4),
                child: VoiceInputWidget(
                  onTextRecognized: (text) {
                    _messageController.text = text;
                  },
                ),
              ),
              
              // Champ de texte étendu
              Expanded(
                child: TextField(
                  controller: _messageController,
                  decoration: const InputDecoration(
                    hintText: 'Décrivez vos symptômes ou posez votre question...',
                    hintStyle: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 16,
                    ),
                    border: InputBorder.none,
                    contentPadding: EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 14,
                    ),
                  ),
                  style: const TextStyle(
                    fontSize: 16,
                    color: AppTheme.textPrimary,
                  ),
                  maxLines: 4,
                  minLines: 1,
                  textInputAction: TextInputAction.send,
                  onSubmitted: _sendMessage,
                ),
              ),
              
              // Bouton d'envoi moderne
              Padding(
                padding: const EdgeInsets.only(right: 4),
                child: Container(
                  decoration: BoxDecoration(
                    color: AppTheme.secondaryColor,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: IconButton(
                    onPressed: () => _sendMessage(_messageController.text),
                    icon: const Icon(Icons.send_rounded),
                    style: IconButton.styleFrom(
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.all(12),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        
        // Barre d'actions rapides (optionnelle)
        const SizedBox(height: 12),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _buildQuickActionChip('🤒 Fièvre', () => _sendMessage('J\'ai de la fièvre')),
            const SizedBox(width: 8),
            _buildQuickActionChip('😷 Toux', () => _sendMessage('J\'ai une toux')),
            const SizedBox(width: 8),
            _buildQuickActionChip('🤕 Mal de tête', () => _sendMessage('J\'ai mal à la tête')),
          ],
        ),
      ],
    );
  }

  Widget _buildQuickActionChip(String text, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: AppTheme.secondaryColor.withOpacity(0.1),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: AppTheme.secondaryColor.withOpacity(0.3),
            width: 1,
          ),
        ),
        child: Text(
          text,
          style: const TextStyle(
            color: AppTheme.secondaryColor,
            fontSize: 12,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    );
  }

  Widget _buildWelcomeCard() {
    return Container(
      width: double.infinity,
      margin: ResponsiveUtils.getResponsivePadding(context),
      padding: EdgeInsets.all(ResponsiveUtils.isDesktop(context) ? AppTheme.spacingXl : AppTheme.spacingLg),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            AppTheme.primaryColor.withOpacity(0.1),
            AppTheme.secondaryColor.withOpacity(0.1),
          ],
        ),
        borderRadius: BorderRadius.circular(AppTheme.radiusLg),
      ),
      child: Column(
        children: [
          Icon(
            Icons.smart_toy,
            size: 48,
            color: AppTheme.primaryColor,
          ),
          const SizedBox(height: 16),
          Text(
            'Bonjour ! Je suis votre assistant médical IA',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            'Décrivez-moi vos symptômes et je vous aiderai à identifier le bon spécialiste et à prendre rendez-vous.',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          Wrap(
            spacing: 8,
            children: [
              _buildSuggestionChip('Mal de gorge'),
              _buildSuggestionChip('Migraine'),
              _buildSuggestionChip('Douleur abdominale'),
              _buildSuggestionChip('Fatigue'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSuggestionChip(String text) {
    return ActionChip(
      label: Text(text),
      onPressed: () => _sendMessage(text),
      backgroundColor: AppTheme.primaryColor.withOpacity(0.1),
    );
  }

  void _sendMessage(String text) {
    if (text.trim().isEmpty) return;

    ref.read(chatProvider.notifier).sendMessage(text.trim());
    _messageController.clear();
  }

  void _handleSpecialtyTap(String specialty) {
    // Charger les médecins de cette spécialité
    ref.read(doctorsProvider.notifier).loadDoctorsBySpecialty(specialty);
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Prendre rendez-vous'),
        content: Text('Voulez-vous consulter la liste des ${specialty.toLowerCase()}s disponibles ?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              _navigateToBookAppointment();
            },
            child: const Text('Voir les médecins'),
          ),
        ],
      ),
    );
  }

  void _navigateToBookAppointment() {
    context.push(AppConstants.appointmentsRoute + '/book');
  }
}
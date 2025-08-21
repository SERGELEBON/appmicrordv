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

            // Zone de saisie
            Container(
              constraints: BoxConstraints(
                maxWidth: ResponsiveUtils.getMaxContentWidth(context),
              ),
              padding: ResponsiveUtils.getResponsivePadding(context),
              decoration: BoxDecoration(
                color: Theme.of(context).cardColor,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 10,
                    offset: const Offset(0, -2),
                  ),
                ],
              ),
              child: SafeArea(
                child: LayoutBuilder(
                  builder: (context, constraints) {
                    final isWideScreen = constraints.maxWidth > 600;
                    
                    if (isWideScreen) {
                      return Row(
                        children: _buildInputWidgets(),
                      );
                    } else {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Row(
                            children: _buildInputWidgets(),
                          ),
                        ],
                      );
                    }
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  List<Widget> _buildInputWidgets() {
    return [
      // Bouton microphone
      VoiceInputWidget(
        onTextRecognized: (text) {
          _messageController.text = text;
        },
      ),
      SizedBox(width: AppTheme.spacingSm),
      
      // Champ de texte
      Expanded(
        child: TextField(
          controller: _messageController,
          decoration: InputDecoration(
            hintText: 'Décrivez vos symptômes...',
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
            contentPadding: EdgeInsets.symmetric(
              horizontal: AppTheme.spacingMd,
              vertical: AppTheme.spacingMd,
            ),
          ),
          maxLines: null,
          textInputAction: TextInputAction.send,
          onSubmitted: _sendMessage,
        ),
      ),
      SizedBox(width: AppTheme.spacingSm),
      
      // Bouton d'envoi
      IconButton(
        onPressed: () => _sendMessage(_messageController.text),
        icon: const Icon(Icons.send),
        style: IconButton.styleFrom(
          backgroundColor: AppTheme.primaryColor,
          foregroundColor: Colors.white,
          padding: EdgeInsets.all(AppTheme.spacingMd),
        ),
      ),
    ];
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
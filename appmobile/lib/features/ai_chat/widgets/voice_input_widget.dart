import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/providers/chat_provider.dart';
import '../../../core/theme/app_theme.dart';

class VoiceInputWidget extends ConsumerStatefulWidget {
  final Function(String) onTextRecognized;

  const VoiceInputWidget({
    super.key,
    required this.onTextRecognized,
  });

  @override
  ConsumerState<VoiceInputWidget> createState() => _VoiceInputWidgetState();
}

class _VoiceInputWidgetState extends ConsumerState<VoiceInputWidget>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 1.2).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final speechState = ref.watch(speechProvider);

    ref.listen(speechProvider, (previous, next) {
      if (next.isListening && !_animationController.isAnimating) {
        _animationController.repeat(reverse: true);
      } else if (!next.isListening && _animationController.isAnimating) {
        _animationController.stop();
        _animationController.reset();
      }

      if (next.recognizedText.isNotEmpty && 
          !next.isListening && 
          previous?.recognizedText != next.recognizedText) {
        widget.onTextRecognized(next.recognizedText);
      }

      if (next.error != null) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.error!),
            backgroundColor: AppTheme.errorColor,
          ),
        );
        ref.read(speechProvider.notifier).clearError();
      }
    });

    return AnimatedBuilder(
      animation: _scaleAnimation,
      builder: (context, child) {
        return Transform.scale(
          scale: speechState.isListening ? _scaleAnimation.value : 1.0,
          child: IconButton(
            onPressed: speechState.isInitialized ? _toggleListening : null,
            icon: Icon(
              speechState.isListening ? Icons.mic : Icons.mic_none,
              color: speechState.isListening ? AppTheme.errorColor : null,
            ),
            style: IconButton.styleFrom(
              backgroundColor: speechState.isListening 
                  ? AppTheme.errorColor.withOpacity(0.1)
                  : AppTheme.primaryColor.withOpacity(0.1),
              foregroundColor: speechState.isListening 
                  ? AppTheme.errorColor 
                  : AppTheme.primaryColor,
            ),
          ),
        );
      },
    );
  }

  void _toggleListening() {
    final speechNotifier = ref.read(speechProvider.notifier);
    final speechState = ref.read(speechProvider);

    if (speechState.isListening) {
      speechNotifier.stopListening();
    } else {
      speechNotifier.startListening();
    }
  }
}
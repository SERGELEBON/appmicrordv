import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/providers/auth_provider.dart';

class EmailVerificationScreen extends ConsumerStatefulWidget {
  final String? token;
  final String? email;
  
  const EmailVerificationScreen({
    super.key,
    this.token,
    this.email,
  });

  @override
  ConsumerState<EmailVerificationScreen> createState() => _EmailVerificationScreenState();
}

class _EmailVerificationScreenState extends ConsumerState<EmailVerificationScreen> {
  bool _isVerifying = false;
  bool _isVerified = false;
  String? _errorMessage;
  bool _canResend = true;
  int _resendCooldown = 0;

  @override
  void initState() {
    super.initState();
    // Si un token est fourni, vérifier automatiquement
    if (widget.token != null) {
      _verifyEmail();
    }
  }

  Future<void> _verifyEmail() async {
    if (widget.token == null) return;
    
    setState(() {
      _isVerifying = true;
      _errorMessage = null;
    });

    try {
      final success = await ref.read(authStateProvider.notifier).verifyEmail(widget.token!);
      
      if (success) {
        setState(() {
          _isVerified = true;
          _isVerifying = false;
        });
        
        // Rediriger vers la page d'accueil après 3 secondes (maintenant connecté)
        Future.delayed(const Duration(seconds: 3), () {
          if (mounted) {
            context.go(AppConstants.homeRoute);
          }
        });
      } else {
        setState(() {
          _isVerifying = false;
          _errorMessage = 'Erreur lors de la vérification. Le lien est peut-être expiré.';
        });
      }
      
    } catch (e) {
      setState(() {
        _isVerifying = false;
        _errorMessage = 'Erreur lors de la vérification. Le lien est peut-être expiré.';
      });
    }
  }

  Future<void> _resendVerificationEmail() async {
    if (!_canResend || widget.email == null) return;
    
    setState(() {
      _canResend = false;
      _resendCooldown = 60;
    });

    try {
      // Utiliser forgotPassword temporairement pour renvoyer email
      // ou créer une nouvelle méthode resendVerification dans le provider
      final success = await ref.read(authStateProvider.notifier).forgotPassword(widget.email!);
      
      if (mounted && success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Email de vérification renvoyé avec succès'),
            backgroundColor: AppTheme.successColor,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Erreur lors de l\'envoi de l\'email'),
            backgroundColor: AppTheme.errorColor,
          ),
        );
      }
    }

    // Démarrer le cooldown
    _startCooldown();
  }

  void _startCooldown() {
    if (_resendCooldown > 0) {
      Future.delayed(const Duration(seconds: 1), () {
        if (mounted) {
          setState(() {
            _resendCooldown--;
            if (_resendCooldown == 0) {
              _canResend = true;
            }
          });
          if (_resendCooldown > 0) {
            _startCooldown();
          }
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Vérification email'),
        automaticallyImplyLeading: widget.token == null,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Icône d'état
                  Container(
                    width: 100,
                    height: 100,
                    decoration: BoxDecoration(
                      color: _isVerified 
                        ? AppTheme.successColor
                        : _errorMessage != null 
                          ? AppTheme.errorColor
                          : AppTheme.primaryColor,
                      shape: BoxShape.circle,
                    ),
                    child: _isVerifying
                      ? const Padding(
                          padding: EdgeInsets.all(30),
                          child: CircularProgressIndicator(
                            color: Colors.white,
                            strokeWidth: 3,
                          ),
                        )
                      : Icon(
                          _isVerified 
                            ? Icons.check
                            : _errorMessage != null 
                              ? Icons.error_outline
                              : Icons.email_outlined,
                          color: Colors.white,
                          size: 60,
                        ),
                  ),
                  
                  const SizedBox(height: 32),
                  
                  // Titre
                  Text(
                    _isVerifying
                      ? 'Vérification en cours...'
                      : _isVerified
                        ? 'Email vérifié !'
                        : _errorMessage != null
                          ? 'Échec de la vérification'
                          : 'Vérifiez votre email',
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: _isVerified 
                        ? AppTheme.successColor
                        : _errorMessage != null 
                          ? AppTheme.errorColor
                          : AppTheme.primaryColor,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  
                  const SizedBox(height: 16),
                  
                  // Message
                  Text(
                    _isVerifying
                      ? 'Validation de votre adresse email en cours...'
                      : _isVerified
                        ? 'Votre adresse email a été vérifiée avec succès. Vous allez être redirigé vers l\'accueil.'
                        : _errorMessage != null
                          ? _errorMessage!
                          : widget.email != null
                            ? 'Un email de vérification a été envoyé à ${widget.email}. Cliquez sur le lien dans l\'email pour activer votre compte.'
                            : 'Suivez les instructions dans l\'email de vérification pour activer votre compte.',
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  
                  if (widget.email != null && !_isVerified && _errorMessage == null) ...[
                    const SizedBox(height: 24),
                    
                    // Email affiché
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: AppTheme.lightOrange,
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      ),
                      child: Row(
                        children: [
                          const Icon(
                            Icons.email_outlined,
                            color: AppTheme.primaryColor,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Text(
                              widget.email!,
                              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                  
                  if (!_isVerified && _errorMessage != null) ...[
                    const SizedBox(height: 24),
                    
                    // Suggestions en cas d'erreur
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.red.shade50,
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                        border: Border.all(color: Colors.red.shade200),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Que faire ?',
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.w600,
                              color: Colors.red.shade800,
                            ),
                          ),
                          const SizedBox(height: 8),
                          _buildSuggestion('Vérifiez que le lien n\'est pas expiré'),
                          _buildSuggestion('Demandez un nouveau lien de vérification'),
                          _buildSuggestion('Contactez le support si le problème persiste'),
                        ],
                      ),
                    ),
                  ],
                ],
              ),
            ),
            
            // Boutons d'action
            if (!_isVerifying) ...[
              if (widget.email != null && !_isVerified)
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _canResend ? _resendVerificationEmail : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.primaryColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      ),
                    ),
                    child: Text(
                      _canResend 
                        ? 'Renvoyer l\'email de vérification'
                        : 'Renvoyer dans ${_resendCooldown}s',
                    ),
                  ),
                ),
              
              const SizedBox(height: 12),
              
              SizedBox(
                width: double.infinity,
                child: OutlinedButton(
                  onPressed: () {
                    context.go(AppConstants.loginRoute);
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: AppTheme.primaryColor,
                    side: const BorderSide(color: AppTheme.primaryColor),
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                    ),
                  ),
                  child: const Text('Aller à la connexion'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
  
  Widget _buildSuggestion(String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('• ', style: TextStyle(fontWeight: FontWeight.bold)),
          Expanded(child: Text(text)),
        ],
      ),
    );
  }
}
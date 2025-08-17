import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/theme/app_theme.dart';

class RegistrationConfirmationScreen extends StatelessWidget {
  final String userRole;
  final String email;
  
  const RegistrationConfirmationScreen({
    super.key,
    required this.userRole,
    required this.email,
  });

  @override
  Widget build(BuildContext context) {
    final isDoctor = userRole == AppConstants.doctorRole;
    
    return Scaffold(
      appBar: AppBar(
        title: const Text('Inscription réussie'),
        automaticallyImplyLeading: false,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Icône de succès
                  Container(
                    width: 100,
                    height: 100,
                    decoration: const BoxDecoration(
                      color: AppTheme.successColor,
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.check,
                      color: Colors.white,
                      size: 60,
                    ),
                  ),
                  
                  const SizedBox(height: 32),
                  
                  // Titre
                  Text(
                    isDoctor 
                      ? 'Inscription en cours de validation'
                      : 'Inscription réussie !',
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: AppTheme.primaryColor,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  
                  const SizedBox(height: 16),
                  
                  // Message principal
                  Text(
                    isDoctor
                      ? 'Votre demande d\'inscription en tant que médecin a été transmise. Vous allez recevoir un email de validation.'
                      : 'Votre compte patient a été créé avec succès. Vous allez recevoir un email de validation.',
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  
                  const SizedBox(height: 24),
                  
                  // Email
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
                            email,
                            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  
                  const SizedBox(height: 32),
                  
                  // Instructions
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Colors.blue.shade50,
                      borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      border: Border.all(color: Colors.blue.shade200),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Prochaines étapes :',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.w600,
                            color: Colors.blue.shade800,
                          ),
                        ),
                        const SizedBox(height: 8),
                        if (isDoctor) ...[
                          _buildStep('1. Vérifiez votre boîte email'),
                          _buildStep('2. Cliquez sur le lien de validation'),
                          _buildStep('3. Attendez la validation administrative'),
                          _buildStep('4. Vous pourrez ensuite vous connecter'),
                        ] else ...[
                          _buildStep('1. Vérifiez votre boîte email'),
                          _buildStep('2. Cliquez sur le lien de validation'),
                          _buildStep('3. Connectez-vous avec vos identifiants'),
                        ],
                      ],
                    ),
                  ),
                  
                  if (isDoctor) ...[
                    const SizedBox(height: 24),
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.orange.shade50,
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons.info_outline,
                            color: Colors.orange.shade700,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Text(
                              'En tant que médecin, votre compte nécessite une validation administrative. Merci pour votre patience.',
                              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                color: Colors.orange.shade800,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ],
              ),
            ),
            
            // Boutons
            Column(
              children: [
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () {
                      context.go(AppConstants.loginRoute);
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.primaryColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      ),
                    ),
                    child: const Text('Aller à la connexion'),
                  ),
                ),
                
                const SizedBox(height: 12),
                
                TextButton(
                  onPressed: () {
                    // TODO: Renvoyer email de validation
                  },
                  child: const Text('Renvoyer l\'email de validation'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildStep(String text) {
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
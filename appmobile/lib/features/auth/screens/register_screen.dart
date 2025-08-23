import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/providers/auth_provider.dart';
import '../../../core/models/user.dart';
import '../../../core/theme/app_theme.dart';
import '../../../core/constants/app_constants.dart';
import '../../../core/widgets/safe_scroll_view.dart';

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  
  // Controllers
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _phoneController = TextEditingController();
  
  // État
  int _currentStep = 0;
  String _selectedRole = AppConstants.patientRole;
  String? _selectedSpecialty;
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;
  bool _acceptTerms = false;

  @override
  void initState() {
    super.initState();
    // Ajouter des listeners pour la validation en temps réel
    _firstNameController.addListener(_updateValidation);
    _lastNameController.addListener(_updateValidation);
    _emailController.addListener(_updateValidation);
    _passwordController.addListener(_updateValidation);
    _confirmPasswordController.addListener(_updateValidation);
  }

  void _updateValidation() {
    setState(() {
      // Déclencher la reconstruction pour mettre à jour le bouton
    });
  }

  // Debug pour vérifier l'état de validation
  void _debugValidation() {
    print('=== DEBUG VALIDATION ===');
    print('Current step: $_currentStep');
    print('First name: "${_firstNameController.text.trim()}"');
    print('Last name: "${_lastNameController.text.trim()}"');
    print('Email: "${_emailController.text.trim()}"');
    print('Password: "${_passwordController.text}"');
    print('Confirm Password: "${_confirmPasswordController.text}"');
    print('Selected role: $_selectedRole');
    print('Selected specialty: $_selectedSpecialty');
    print('Is valid: ${_validateCurrentStep()}');
    print('=======================');
  }

  @override
  void dispose() {
    _firstNameController.removeListener(_updateValidation);
    _lastNameController.removeListener(_updateValidation);
    _emailController.removeListener(_updateValidation);
    _passwordController.removeListener(_updateValidation);
    _confirmPasswordController.removeListener(_updateValidation);
    
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < 2) {
      setState(() => _currentStep++);
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      setState(() => _currentStep--);
    }
  }

  Widget _getCurrentStepWidget() {
    switch (_currentStep) {
      case 0:
        return Container(
          key: const ValueKey('step1'),
          child: _buildStep1(),
        );
      case 1:
        return Container(
          key: const ValueKey('step2'),
          child: _buildStep2(),
        );
      case 2:
        return Container(
          key: const ValueKey('step3'),
          child: _buildStep3(),
        );
      default:
        return Container(
          key: const ValueKey('step1'),
          child: _buildStep1(),
        );
    }
  }

  bool _validateCurrentStep() {
    switch (_currentStep) {
      case 0:
        // Pour les médecins, la spécialité doit être sélectionnée
        if (_selectedRole == AppConstants.doctorRole) {
          return _selectedSpecialty != null && _selectedSpecialty!.isNotEmpty;
        }
        return true; // Pour les patients, toujours valide
      case 1:
        return _firstNameController.text.trim().isNotEmpty && 
               _lastNameController.text.trim().isNotEmpty;
      case 2:
        return _emailController.text.trim().isNotEmpty && 
               RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(_emailController.text.trim()) &&
               _passwordController.text.length >= 6 &&
               _passwordController.text == _confirmPasswordController.text &&
               _acceptTerms;
      default:
        return false;
    }
  }

  Future<void> _register() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    try {
      final request = RegisterRequest(
        email: _emailController.text.trim(),
        password: _passwordController.text,
        firstName: _firstNameController.text.trim(),
        lastName: _lastNameController.text.trim(),
        role: _selectedRole,
        phone: _phoneController.text.trim().isEmpty ? null : _phoneController.text.trim(),
        specialty: _selectedSpecialty,
      );
      
      final success = await ref.read(authStateProvider.notifier).register(request);
      
      if (success && mounted) {
        // Succès : rediriger vers la page de validation
        context.go('/email-verification?email=${Uri.encodeComponent(_emailController.text.trim())}');
      } else if (mounted) {
        // Échec : rester sur la page avec un message d'erreur du provider
        final errorMessage = ref.read(authStateProvider).error ?? 
                            'Échec de l\'inscription. Veuillez réessayer.';
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(errorMessage),
            backgroundColor: AppTheme.errorColor,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Erreur lors de l\'inscription: ${e.toString()}'),
            backgroundColor: AppTheme.errorColor,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authStateProvider);
    
    ref.listen(authStateProvider, (previous, next) {
      // Nettoyer les erreurs après un délai pour éviter qu'elles s'accumulent
      if (next.error != null && mounted) {
        Future.delayed(const Duration(seconds: 5), () {
          if (mounted) {
            ref.read(authStateProvider.notifier).clearError();
          }
        });
      }
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Inscription'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: _currentStep == 0 
            ? () => context.go(AppConstants.loginRoute)
            : _previousStep,
        ),
      ),
      body: Column(
        children: [
          // Indicateur de progression
          Container(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: List.generate(3, (index) {
                final isActive = index <= _currentStep;
                final isCompleted = index < _currentStep;
                
                return Expanded(
                  child: Container(
                    margin: EdgeInsets.only(
                      right: index == 2 ? 0 : 8,
                    ),
                    child: Column(
                      children: [
                        Container(
                          height: 4,
                          decoration: BoxDecoration(
                            color: isActive 
                              ? AppTheme.primaryColor 
                              : AppTheme.secondaryColor.withValues(alpha: 0.3),
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                        const SizedBox(height: 8),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Container(
                              width: 24,
                              height: 24,
                              decoration: BoxDecoration(
                                color: isCompleted 
                                  ? AppTheme.primaryColor
                                  : isActive 
                                    ? AppTheme.primaryColor
                                    : AppTheme.secondaryColor.withValues(alpha: 0.3),
                                shape: BoxShape.circle,
                              ),
                              child: isCompleted 
                                ? const Icon(Icons.check, color: Colors.white, size: 16)
                                : Center(
                                    child: Text(
                                      '${index + 1}',
                                      style: TextStyle(
                                        color: isActive ? Colors.white : AppTheme.textSecondary,
                                        fontSize: 12,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                );
              }),
            ),
          ),
          
          // Contenu des étapes
          Expanded(
            child: SafeScrollView(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: Form(
                  key: _formKey,
                  child: AnimatedSwitcher(
                    duration: const Duration(milliseconds: 300),
                    transitionBuilder: (Widget child, Animation<double> animation) {
                      return SlideTransition(
                        position: animation.drive(
                          Tween<Offset>(
                            begin: const Offset(1.0, 0.0),
                            end: Offset.zero,
                          ).chain(CurveTween(curve: Curves.easeInOut)),
                        ),
                        child: child,
                      );
                    },
                    child: _getCurrentStepWidget(),
                  ),
                ),
              ),
            ),
          ),
          
          // Boutons de navigation
          Container(
            padding: const EdgeInsets.all(24),
            child: Row(
              children: [
                if (_currentStep > 0)
                  Expanded(
                    child: OutlinedButton(
                      onPressed: _previousStep,
                      style: OutlinedButton.styleFrom(
                        foregroundColor: AppTheme.blueAccent,
                        side: const BorderSide(color: AppTheme.blueAccent),
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                        ),
                      ),
                      child: const Text('Précédent'),
                    ),
                  ),
                if (_currentStep > 0) const SizedBox(width: 16),
                Expanded(
                  flex: _currentStep == 0 ? 1 : 1,
                  child: ElevatedButton(
                    onPressed: _currentStep == 2 
                      ? (authState.isLoading ? null : _register)
                      : (_validateCurrentStep() ? _nextStep : null),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.primaryColor,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(AppTheme.radiusMd),
                      ),
                    ),
                    child: authState.isLoading && _currentStep == 2
                      ? const SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                          ),
                        )
                      : Text(_currentStep == 2 ? 'Créer le compte' : 'Suivant'),
                  ),
                ),
              ],
            ),
          ),
          
          // Lien vers connexion (en bas)
          Container(
            padding: const EdgeInsets.only(bottom: 24),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text('Déjà un compte ? '),
                TextButton(
                  onPressed: () {
                    context.go(AppConstants.loginRoute);
                  },
                  child: Text(
                    'Se connecter',
                    style: TextStyle(
                      color: AppTheme.blueAccent,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // Étape 1 : Type de compte
  Widget _buildStep1() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Quel type de compte souhaitez-vous ?',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: AppTheme.primaryColor,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Sélectionnez le type de compte qui correspond à votre profil',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 32),

        // Cartes de sélection
        _buildAccountTypeCard(
          title: 'Patient',
          subtitle: 'Je cherche des consultations médicales',
          icon: Icons.person,
          value: AppConstants.patientRole,
          isSelected: _selectedRole == AppConstants.patientRole,
        ),
        const SizedBox(height: 16),
        
        _buildAccountTypeCard(
          title: 'Médecin',
          subtitle: 'Je suis un professionnel de santé',
          icon: Icons.medical_services,
          value: AppConstants.doctorRole,
          isSelected: _selectedRole == AppConstants.doctorRole,
        ),

        // Spécialité pour les médecins
        if (_selectedRole == AppConstants.doctorRole) ...[
          const SizedBox(height: 24),
          Text(
            'Spécialité médicale',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          DropdownButtonFormField<String>(
            value: _selectedSpecialty,
            decoration: InputDecoration(
              prefixIcon: const Icon(Icons.medical_services_outlined),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(AppTheme.radiusMd),
              ),
            ),
            items: AppConstants.medicalSpecialties
                .map((specialty) => DropdownMenuItem(
                      value: specialty,
                      child: Text(specialty),
                    ))
                .toList(),
            onChanged: (value) {
              setState(() {
                _selectedSpecialty = value;
              });
            },
          ),
        ],
      ],
    );
  }

  Widget _buildAccountTypeCard({
    required String title,
    required String subtitle,
    required IconData icon,
    required String value,
    required bool isSelected,
  }) {
    return GestureDetector(
      onTap: () {
        setState(() {
          _selectedRole = value;
          if (value != AppConstants.doctorRole) {
            _selectedSpecialty = null;
          }
        });
      },
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: isSelected 
            ? AppTheme.lightOrange 
            : Colors.white,
          border: Border.all(
            color: isSelected 
              ? AppTheme.primaryColor 
              : AppTheme.secondaryColor.withValues(alpha: 0.3),
            width: isSelected ? 2 : 1,
          ),
          borderRadius: BorderRadius.circular(AppTheme.radiusMd),
        ),
        child: Row(
          children: [
            Container(
              width: 48,
              height: 48,
              decoration: BoxDecoration(
                color: isSelected 
                  ? AppTheme.primaryColor 
                  : AppTheme.secondaryColor.withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(24),
              ),
              child: Icon(
                icon,
                color: isSelected 
                  ? Colors.white 
                  : AppTheme.primaryColor,
                size: 24,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: isSelected ? AppTheme.primaryColor : null,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
            ),
            if (isSelected)
              Container(
                width: 24,
                height: 24,
                decoration: const BoxDecoration(
                  color: AppTheme.primaryColor,
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.check,
                  color: Colors.white,
                  size: 16,
                ),
              ),
          ],
        ),
      ),
    );
  }

  // Étape 2 : Informations personnelles
  Widget _buildStep2() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Vos informations personnelles',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: AppTheme.primaryColor,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Ces informations nous permettront de personnaliser votre expérience',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 32),

        // Prénom
        TextFormField(
          controller: _firstNameController,
          decoration: InputDecoration(
            labelText: 'Prénom',
            prefixIcon: const Icon(Icons.person_outline),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Veuillez entrer votre prénom';
            }
            return null;
          },
        ),
        const SizedBox(height: 16),

        // Nom
        TextFormField(
          controller: _lastNameController,
          decoration: InputDecoration(
            labelText: 'Nom de famille',
            prefixIcon: const Icon(Icons.person_outline),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Veuillez entrer votre nom';
            }
            return null;
          },
        ),
        const SizedBox(height: 16),

        // Téléphone
        TextFormField(
          controller: _phoneController,
          keyboardType: TextInputType.phone,
          decoration: InputDecoration(
            labelText: 'Téléphone (optionnel)',
            prefixIcon: const Icon(Icons.phone_outlined),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
        ),
      ],
    );
  }

  // Étape 3 : Connexion
  Widget _buildStep3() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Créez vos identifiants',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: AppTheme.primaryColor,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Ces informations vous permettront de vous connecter à votre compte',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 32),

        // Email
        TextFormField(
          controller: _emailController,
          keyboardType: TextInputType.emailAddress,
          decoration: InputDecoration(
            labelText: 'Adresse email',
            prefixIcon: const Icon(Icons.email_outlined),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Veuillez entrer votre email';
            }
            if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
              return 'Veuillez entrer un email valide';
            }
            return null;
          },
        ),
        const SizedBox(height: 16),

        // Mot de passe
        TextFormField(
          controller: _passwordController,
          obscureText: _obscurePassword,
          decoration: InputDecoration(
            labelText: 'Mot de passe',
            prefixIcon: const Icon(Icons.lock_outlined),
            suffixIcon: IconButton(
              icon: Icon(
                _obscurePassword ? Icons.visibility : Icons.visibility_off,
              ),
              onPressed: () {
                setState(() {
                  _obscurePassword = !_obscurePassword;
                });
              },
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Veuillez entrer un mot de passe';
            }
            if (value.length < 6) {
              return 'Le mot de passe doit contenir au moins 6 caractères';
            }
            return null;
          },
        ),
        const SizedBox(height: 16),

        // Confirmation mot de passe
        TextFormField(
          controller: _confirmPasswordController,
          obscureText: _obscureConfirmPassword,
          decoration: InputDecoration(
            labelText: 'Confirmer le mot de passe',
            prefixIcon: const Icon(Icons.lock_outlined),
            suffixIcon: IconButton(
              icon: Icon(
                _obscureConfirmPassword ? Icons.visibility : Icons.visibility_off,
              ),
              onPressed: () {
                setState(() {
                  _obscureConfirmPassword = !_obscureConfirmPassword;
                });
              },
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            ),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Veuillez confirmer votre mot de passe';
            }
            if (value != _passwordController.text) {
              return 'Les mots de passe ne correspondent pas';
            }
            return null;
          },
        ),

        const SizedBox(height: 24),

        // Case à cocher pour les conditions d'utilisation
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: Colors.grey[50],
            borderRadius: BorderRadius.circular(AppTheme.radiusMd),
            border: Border.all(
              color: Colors.grey[300]!,
              width: 1,
            ),
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Checkbox(
                value: _acceptTerms,
                onChanged: (value) {
                  setState(() {
                    _acceptTerms = value ?? false;
                  });
                },
                activeColor: AppTheme.secondaryColor,
              ),
              Expanded(
                child: GestureDetector(
                  onTap: () {
                    setState(() {
                      _acceptTerms = !_acceptTerms;
                    });
                  },
                  child: Padding(
                    padding: const EdgeInsets.only(top: 12),
                    child: RichText(
                      text: TextSpan(
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          height: 1.4,
                        ),
                        children: [
                          const TextSpan(text: 'J\'accepte les '),
                          WidgetSpan(
                            child: GestureDetector(
                              onTap: () => _showTermsOfService(),
                              child: Text(
                                'conditions d\'utilisation',
                                style: TextStyle(
                                  color: AppTheme.secondaryColor,
                                  fontWeight: FontWeight.w600,
                                  decoration: TextDecoration.underline,
                                ),
                              ),
                            ),
                          ),
                          const TextSpan(text: ' et la '),
                          WidgetSpan(
                            child: GestureDetector(
                              onTap: () => _showPrivacyPolicy(),
                              child: Text(
                                'politique de confidentialité',
                                style: TextStyle(
                                  color: AppTheme.secondaryColor,
                                  fontWeight: FontWeight.w600,
                                  decoration: TextDecoration.underline,
                                ),
                              ),
                            ),
                          ),
                          const TextSpan(text: ' de RDV Chez Doc.'),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),

        const SizedBox(height: 24),

        // Résumé du compte
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppTheme.lightOrange,
            borderRadius: BorderRadius.circular(AppTheme.radiusMd),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Récapitulatif du compte',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  fontWeight: FontWeight.w600,
                  color: AppTheme.primaryColor,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Type: ${_selectedRole == AppConstants.doctorRole ? "Médecin" : "Patient"}',
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              if (_selectedSpecialty != null)
                Text(
                  'Spécialité: $_selectedSpecialty',
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              Text(
                'Nom: ${_firstNameController.text} ${_lastNameController.text}',
                style: Theme.of(context).textTheme.bodyMedium,
              ),
            ],
          ),
        ),
      ],
    );
  }

  void _showTermsOfService() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Conditions d\'utilisation'),
        content: const SingleChildScrollView(
          child: Text(
            'En utilisant RDV Chez Doc, vous acceptez les conditions suivantes :\n\n'
            '1. Vous utilisez cette application à des fins médicales légitimes\n'
            '2. Vous fournirez des informations exactes et à jour\n'
            '3. Vous respecterez la confidentialité des autres utilisateurs\n'
            '4. Vous ne partagerez pas vos identifiants de connexion\n'
            '5. En cas d\'urgence, vous contacterez directement les services d\'urgence\n\n'
            'Ces conditions peuvent être mises à jour à tout moment.',
            style: TextStyle(height: 1.5),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer'),
          ),
        ],
      ),
    );
  }

  void _showPrivacyPolicy() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Politique de confidentialité'),
        content: const SingleChildScrollView(
          child: Text(
            'RDV Chez Doc s\'engage à protéger vos données personnelles :\n\n'
            '• Vos données médicales sont cryptées et sécurisées\n'
            '• Nous ne partageons jamais vos informations sans votre consentement\n'
            '• Vous pouvez demander la suppression de vos données à tout moment\n'
            '• Nous utilisons des cookies pour améliorer votre expérience\n'
            '• Vos données sont stockées conformément au RGPD\n\n'
            'Pour plus d\'informations, contactez notre équipe de support.',
            style: TextStyle(height: 1.5),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Fermer'),
          ),
        ],
      ),
    );
  }
}
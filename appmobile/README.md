# MediApp - Application Mobile de Gestion Médicale avec IA

Une application Flutter complète pour la gestion de rendez-vous médicaux avec intelligence artificielle intégrée.

## 🚀 Fonctionnalités Principales

### A. Authentification et Sécurité ✅
- Création de compte (Patient/Médecin)
- Connexion sécurisée via JWT
- Réinitialisation de mot de passe
- Sécurité API via Gateway

### B. Gestion des Rendez-vous ✅
- Prise de rendez-vous avec agenda des médecins
- Notifications de rappel
- Historique des rendez-vous
- Validation et annulation de rendez-vous

### C. IA Médicale Intégrée ✅

#### Use Case 1 - Assistant Médical IA (Chatbot)
- Description des symptômes par le patient
- Recommandations IA de spécialistes
- Prise de rendez-vous automatique

#### Use Case 2 - Résumé Automatique de Consultation
- Résumé automatique des consultations
- Génération de notes médicales
- Intégration vocale

#### Use Case 3 - Pré-diagnostic IA
- Questionnaire intelligent de symptômes
- Suggestions de causes possibles
- Recommandations d'examens

#### Use Case 4 - Suivi Post-consultation
- Recommandations personnalisées
- Rappels de traitement
- Conseils santé IA

#### Use Case 5 - IA de Priorisation
- Optimisation automatique des créneaux
- Gestion des urgences
- Priorisation intelligente

### D. Tableau de Bord Utilisateur ✅
- Profil utilisateur personnalisé
- Historique des rendez-vous
- Notifications et messages IA
- Conseils santé personnalisés

## 🛠 Installation et Configuration

### Prérequis
- Flutter SDK 3.8.1+
- Dart SDK
- Android Studio / VS Code
- Émulateur Android ou appareil physique

### Étapes d'installation

1. **Cloner et installer les dépendances**
```bash
cd appmobile
flutter pub get
```

2. **Générer les fichiers de code**
```bash
flutter packages pub run build_runner build --delete-conflicting-outputs
```

3. **Configuration des services backend**
Modifier les URLs dans `lib/core/constants/app_constants.dart` :
```dart
static const String authServiceUrl = 'http://votre-ip:8081/api/auth';
static const String rdvServiceUrl = 'http://votre-ip:8082/api/rdv';
static const String chatServiceUrl = 'http://votre-ip:8083/api/chat';
```

4. **Lancer l'application**
```bash
flutter run
```

## 📱 Architecture de l'Application

### Structure des dossiers
```
lib/
├── core/
│   ├── constants/        # Constantes de l'app
│   ├── models/          # Modèles de données
│   ├── providers/       # Gestion d'état (Riverpod)
│   ├── services/        # Services API (Retrofit)
│   ├── theme/           # Thème et styles
│   └── router/          # Navigation (GoRouter)
├── features/
│   ├── auth/            # Authentification
│   ├── appointments/    # Gestion RDV
│   ├── ai_chat/         # Chat IA
│   ├── ai_diagnosis/    # Diagnostic IA
│   └── dashboard/       # Tableau de bord
└── main.dart           # Point d'entrée
```

### Technologies utilisées
- **Framework**: Flutter 3.8.1+
- **Gestion d'état**: Riverpod
- **Navigation**: GoRouter avec navigation adaptative
- **API**: Dio + Retrofit
- **Storage**: Flutter Secure Storage
- **UI**: Material Design 3 responsive
- **Internationalisation**: Intl (français)
- **Responsive**: Breakpoints adaptatifs (mobile/tablette/desktop)

## 🔐 Sécurité

- Stockage sécurisé des tokens JWT
- Refresh automatique des tokens
- Chiffrement des données sensibles
- Validation côté client et serveur

## 🎨 Interface Utilisateur

### Design Responsive
- **Mobile** (< 600px): Bottom Navigation Bar, layout optimisé tactile
- **Tablette** (600-900px): Navigation Rail latérale, grilles 3-4 colonnes
- **Desktop** (> 900px): Navigation Rail avec labels, layout centré max 1200px
- Breakpoints automatiques et adaptatifs

### Thème & Accessibilité
- Design moderne Material 3 avec spacing cohérent
- Mode sombre/clair automatique selon système
- Couleurs médicales avec contraste optimal
- Tailles de police et icônes adaptatives
- Prévention automatique des RenderFlow errors

### Navigation Adaptative
- **Mobile**: Bottom Navigation Bar 5 onglets
- **Tablette/Desktop**: Navigation Rail latérale
- Différenciation Patient/Médecin automatique
- Gestion d'état de navigation persistante

## 🔧 Configuration pour Production

### Android
1. Configurer la signature de l'app dans `android/app/build.gradle`
2. Modifier l'URL de base pour la production
3. Activer l'obfuscation du code

### iOS
1. Configurer le provisioning profile
2. Modifier les permissions dans Info.plist
3. Configurer l'App Store Connect

## 📋 Points d'attention

### Permissions requises
- **Microphone**: Pour la reconnaissance vocale
- **Internet**: Pour les appels API
- **Stockage**: Pour le cache local

### Intégration Backend
L'application est conçue pour s'intégrer avec :
- Service d'authentification (port 8081)
- Service de gestion RDV (port 8082)  
- Service de chat IA (port 8083)
- Gateway API (port 8080)

### Tests
- Tests unitaires des providers
- Tests d'intégration des services API
- Tests de widgets pour l'UI

## 🚦 État de Développement

✅ **Complété**:
- Architecture et dépendances Flutter
- Authentification et sécurité JWT
- Gestion complète des rendez-vous
- IA médicale (5 use cases implémentés)
- Tableau de bord utilisateur responsive
- Navigation adaptative (mobile/tablette/desktop)
- **Design responsive multi-écrans**
- **Prévention des RenderFlow errors**
- **Breakpoints adaptatifs automatiques**
- **Widgets de sécurité anti-overflow**

🔄 **À finaliser**:
- Génération des fichiers Freezed/JSON
- Tests unitaires et d'intégration
- Configuration de production
- Notifications push
- Tests sur devices physiques

## 📞 Support

Cette application fait partie d'un écosystème complet de gestion médicale. Pour un support complet, assurez-vous que tous les microservices backend sont opérationnels.

---

**MediApp v1.0.0** - Assistant Médical Intelligent
Développé avec Flutter et intégration IA avancée.
# 🚀 Instructions de Lancement - MediApp

## ⚡ Lancement Rapide

### Prérequis
- Flutter SDK 3.8.1+ installé
- Android Studio ou VS Code avec extensions Flutter
- Émulateur Android ou appareil physique connecté

### Commandes de Lancement

```bash
# 1. Aller dans le dossier du projet
cd /home/serge/Téléchargements/microtest/appmobile

# 2. Installer les dépendances
flutter pub get

# 3. Générer les fichiers manquants (si nécessaire)
flutter packages pub run build_runner build --delete-conflicting-outputs

# 4. Lancer l'application
flutter run
```

## 🔧 Configuration Backend

### URLs des Services (à modifier dans `lib/core/constants/app_constants.dart`)

```dart
// Configuration locale de développement
static const String authServiceUrl = 'http://localhost:8081/api/auth';
static const String rdvServiceUrl = 'http://localhost:8082/api/rdv';  
static const String chatServiceUrl = 'http://localhost:8083/api/chat';

// Pour Android Emulator, remplacer localhost par:
static const String authServiceUrl = 'http://10.0.2.2:8081/api/auth';
static const String rdvServiceUrl = 'http://10.0.2.2:8082/api/rdv';
static const String chatServiceUrl = 'http://10.0.2.2:8083/api/chat';

// Pour tests sur réseau local, remplacer par votre IP:
static const String authServiceUrl = 'http://192.168.1.XXX:8081/api/auth';
static const String rdvServiceUrl = 'http://192.168.1.XXX:8082/api/rdv';
static const String chatServiceUrl = 'http://192.168.1.XXX:8083/api/chat';
```

## 📱 Tests Responsive

### Tester différentes tailles d'écran dans Flutter Inspector

1. **Mobile Portrait**: 400x800
2. **Mobile Landscape**: 800x400  
3. **Tablette Portrait**: 800x1200
4. **Tablette Landscape**: 1200x800
5. **Desktop**: 1400x900

### Commandes de Test

```bash
# Tests unitaires
flutter test

# Tests d'intégration
flutter test integration_test/

# Lancer avec device spécifique
flutter devices
flutter run -d <device-id>

# Lancer en mode release
flutter run --release
```

## 🎯 Fonctionnalités à Tester

### ✅ Authentification
- [ ] Inscription Patient/Médecin
- [ ] Connexion avec JWT
- [ ] Déconnexion
- [ ] Validation des formulaires

### ✅ Navigation Responsive  
- [ ] Bottom Navigation (mobile)
- [ ] Navigation Rail (tablette/desktop)
- [ ] Transitions entre écrans
- [ ] Retour en arrière

### ✅ Rendez-vous
- [ ] Sélection spécialité médecin
- [ ] Choix de créneaux
- [ ] Confirmation RDV
- [ ] Historique des RDV

### ✅ Chat IA
- [ ] Interface de conversation
- [ ] Reconnaissance vocale
- [ ] Suggestions de spécialités
- [ ] Prise de RDV depuis chat

### ✅ Pré-diagnostic IA
- [ ] Sélection de symptômes
- [ ] Analyse IA
- [ ] Recommandations
- [ ] Niveaux d'urgence

### ✅ Design Responsive
- [ ] Adaptation mobile ↔ tablette ↔ desktop
- [ ] Rotation d'écran fluide
- [ ] Pas de débordement de contenu
- [ ] Tailles de police adaptatives

## 🐛 Debugging

### Erreurs Communes

1. **Erreur de dépendances**:
```bash
flutter clean
flutter pub get
```

2. **Erreur Freezed/JSON**:
```bash
flutter packages pub run build_runner clean
flutter packages pub run build_runner build --delete-conflicting-outputs
```

3. **Erreur d'émulateur**:
```bash
flutter doctor -v
flutter devices
```

4. **Erreur de réseau**:
- Vérifier que les services backend sont démarrés
- Modifier les URLs dans `app_constants.dart`
- Désactiver HTTPS si nécessaire

### Logs de Debug

```bash
# Logs en temps réel
flutter logs

# Logs avec filter
flutter logs | grep -E "(ERROR|EXCEPTION)"
```

## 🎨 Test du Design

### Points de Contrôle Design

1. **Spacing cohérent**: Utilisation des constantes AppTheme
2. **Couleurs médicales**: Palette bleu/turquoise cohérente  
3. **Typography responsive**: Tailles adaptées aux écrans
4. **Cards uniformes**: Bordures et ombres cohérentes
5. **Icons adaptatives**: Tailles proportionnelles

### Validation UX

1. **Navigation intuitive**: Maximum 3 taps pour toute action
2. **Feedback visuel**: Loading states et confirmations
3. **Accessibilité**: Contrastes et tailles de touch targets
4. **Performance**: Transitions fluides < 300ms

## 📋 Checklist de Validation

### Avant Production

- [ ] Tests sur 3 tailles d'écran minimum
- [ ] Connexion/déconnexion fonctionne
- [ ] Toutes les APIs backend connectées
- [ ] Pas d'erreurs dans les logs Flutter
- [ ] Navigation responsive validée
- [ ] Design cohérent sur tous les écrans
- [ ] Pas de RenderFlow errors
- [ ] Performance acceptable (< 2s cold start)

### Configuration Production

- [ ] URLs backend de production
- [ ] Clés API de production
- [ ] Signature Android configurée
- [ ] Provisioning iOS configuré
- [ ] Obfuscation activée
- [ ] Crash reporting configuré

---

## 🎯 Commande de Lancement Finale

```bash
cd /home/serge/Téléchargements/microtest/appmobile && flutter run
```

**L'application MediApp est prête pour le test et le développement !**
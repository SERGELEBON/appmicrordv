# 📊 GUIDE DE TEST DES PERFORMANCES

## Tests à effectuer après les optimisations

### 1. **Test de Démarrage**
```bash
flutter run --profile
# Mesurer le temps entre splash screen et home screen
# Objectif: < 3 secondes
```

### 2. **Test de Navigation**
- Naviguer entre les écrans rapidement
- Vérifier l'absence de lag
- Objectif: < 300ms par transition

### 3. **Test du Chat IA**
- Envoyer plusieurs messages rapidement
- Vérifier le scroll fluide
- Tester la reconnaissance vocale

### 4. **Test de Cache**
```dart
// Vérifier dans les DevTools que les requêtes sont mises en cache
// Aller dans Network tab et voir les requêtes évitées
```

### 5. **Test Mémoire**
```bash
flutter run --profile
# Observer dans DevTools > Memory
# Objectif: Pas de fuites mémoire visibles
```

## Commandes utiles

### Analyse des performances
```bash
flutter analyze
flutter build apk --analyze-size
```

### DevTools
```bash
flutter run --profile
# Puis ouvrir DevTools pour analyser
```

## Métriques cibles

| Métrique | Avant | Objectif Après |
|----------|-------|----------------|
| Temps démarrage | ~5s | <3s |
| Navigation | ~800ms | <300ms |
| Scroll chat | Lag visible | Fluide 60fps |
| Utilisation mémoire | ~200MB | <150MB |
| Requêtes réseau | Répétées | Cache hits >80% |

## Points de contrôle

- ✅ Select() utilisé partout
- ✅ RepaintBoundary ajouté
- ✅ ListView optimisé
- ✅ Cache réseau actif
- ✅ Speech async
- ✅ Timeouts augmentés
# Auth Service - Microservice d'Authentification

## Description
Microservice d'authentification sécurisé développé avec Spring Boot, fournissant des fonctionnalités d'inscription, connexion, gestion des tokens JWT et refresh tokens pour une architecture microservices.

## Fonctionnalités

### 🔐 Authentification
- **Inscription** (`POST /api/auth/signup`) - Création de nouveaux comptes utilisateur
- **Connexion** (`POST /api/auth/signin`) - Authentification avec JWT + Refresh Token  
- **Déconnexion** (`POST /api/auth/signout`) - Déconnexion sécurisée
- **Refresh Token** (`POST /api/auth/refresh`) - Renouvellement des tokens d'accès
- **Validation Token** (`POST /api/auth/validate`) - Validation des tokens pour autres microservices

### 👥 Gestion des utilisateurs
- **CRUD utilisateurs** - Gestion complète des utilisateurs (Admin uniquement)
- **Rôles** : ADMIN, DOCTOR, PATIENT
- **Historique** - Traçabilité des actions utilisateur (login, logout, etc.)

### 🔒 Sécurité
- **BCrypt** - Hashage sécurisé des mots de passe (12 rounds par défaut)
- **JWT** - Tokens d'authentification avec expiration configurable
- **Refresh Tokens** - Tokens de renouvellement avec nettoyage automatique
- **CORS** - Configuration cross-origin sécurisée
- **Spring Security** - Protection des endpoints

## Architecture

```
src/main/java/ci/hardwork/authservice/
├── config/           # Configuration (Swagger, Scheduler)
├── core/
│   ├── dto/         # Data Transfer Objects
│   ├── mapper/      # MapStruct mappers
│   ├── models/      # Entités JPA
│   │   └── enums/   # Énumérations
│   ├── repository/  # Repositories JPA
│   └── service/     # Services métier
├── exception/       # Gestion globale des exceptions
├── security/        # Configuration JWT et sécurité
└── web/
    └── controller/  # Contrôleurs REST
```

## Technologies

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (jjwt 0.11.5)**
- **MapStruct 1.5.5**
- **Swagger/OpenAPI 3**
- **Lombok**

## Configuration

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|---------|
| `DB_URL` | URL base de données | `jdbc:postgresql://localhost:5432/auth_db` |
| `DB_USERNAME` | Nom utilisateur DB | `postgres` |
| `DB_PASSWORD` | Mot de passe DB | `postgres` |
| `JWT_SECRET` | Clé secrète JWT | (base64) |
| `JWT_EXPIRATION` | Expiration token (ms) | `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION` | Expiration refresh (ms) | `604800000` (7j) |
| `BCRYPT_ROUNDS` | Rounds BCrypt | `12` |
| `CORS_ORIGINS` | Origines CORS autorisées | `http://localhost:3000,http://localhost:4200` |

### Profils disponibles
- `dev` - Développement (DDL create-drop, logs DEBUG)
- `prod` - Production (DDL validate, logs WARN)

## Démarrage

### Prérequis
- Java 17+
- PostgreSQL 12+
- Maven 3.8+

### Installation
```bash
# Cloner le projet
git clone <repository-url>
cd auth-service

# Créer la base de données
createdb auth_db

# Compiler et démarrer
mvn clean spring-boot:run
```

### Base de données
Les tables sont créées automatiquement au démarrage :
- `users` - Utilisateurs
- `user_authorities` - Rôles utilisateur  
- `user_history` - Historique des actions
- `refresh_tokens` - Tokens de renouvellement

## API Documentation

### Swagger UI
Une fois l'application démarrée : `http://localhost:8081/api/swagger-ui.html`

### Endpoints principaux

#### Authentification
```http
POST /api/auth/signup      # Inscription
POST /api/auth/signin      # Connexion  
POST /api/auth/signout     # Déconnexion
POST /api/auth/refresh     # Renouvellement token
POST /api/auth/validate    # Validation token (pour autres services)
```

#### Utilisateurs (Authentification requise)
```http
GET    /api/users          # Liste utilisateurs (Admin)
PUT    /api/users/{id}     # Modifier utilisateur (Admin)
DELETE /api/users/{id}     # Supprimer utilisateur (Admin)
```

#### Santé
```http
GET /api/actuator/health   # Status de l'application
GET /api/actuator/info     # Informations application
```

## Utilisation dans d'autres microservices

### Validation de token
```java
// Appel vers auth-service pour valider un token
POST /api/auth/validate
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// Réponse
{
    "valid": true,
    "username": "john.doe",
    "email": "john@example.com", 
    "authorities": ["PATIENT"],
    "userId": 123
}
```

### Headers d'authentification
```http
Authorization: Bearer <jwt-token>
```

## Sécurité

### Mots de passe
- Hashage BCrypt avec 12 rounds (configurable)
- Validation des critères de complexité côté client

### Tokens JWT
- Expiration : 24h (configurable)
- Algorithme : HS256
- Claims : username, authorities, expiration

### Refresh Tokens
- Expiration : 7 jours (configurable)
- UUID unique par utilisateur
- Nettoyage automatique (tâche programmée)

## Monitoring

### Actuator endpoints
- `/api/actuator/health` - Santé de l'application
- `/api/actuator/metrics` - Métriques de performance
- `/api/actuator/info` - Informations build

### Logs
- Format configurable par profil
- Niveaux ajustables par package
- Rotation automatique en production

## Production

### Optimisations
- Pool de connexions Hikari optimisé
- Batch processing Hibernate
- Compression GZIP
- Cache de second niveau (optionnel)

### Sécurité production
- Secrets externalisés
- HTTPS obligatoire
- Rate limiting (recommandé)
- Monitoring des tentatives d'intrusion

## Support

Pour toute question ou problème :
- Email : contact@hardwork.ci
- Documentation : `/api/swagger-ui.html`
- Logs : Niveau INFO par défaut, DEBUG en développement
# ChatAI Microservice

Service de chat basé sur l'IA utilisant Ollama3 pour des conversations intelligentes avec gestion complète des utilisateurs et des sessions.

## 🚀 Fonctionnalités

### Core Features
- **Chat Intelligence**: Intégration avec Ollama3 LLM pour des conversations naturelles
- **Gestion des Conversations**: Création, modification, archivage et recherche de conversations
- **Historique Complet**: Sauvegarde et récupération de l'historique des messages
- **Authentification JWT**: Intégration avec le service auth-service pour la sécurité
- **Rate Limiting**: Protection contre les abus avec limites de requêtes configurables
- **Sessions Utilisateur**: Gestion avancée des sessions avec tracking des tokens et requêtes

### Features Techniques
- **API REST** avec documentation Swagger/OpenAPI
- **Validation** automatique des données d'entrée
- **Gestion d'erreurs** globale avec messages en français
- **Cache intelligent** avec Caffeine pour optimiser les performances
- **Streaming SSE** pour les réponses en temps réel
- **Tests unitaires** complets avec couverture élevée

## 🏗️ Architecture

```
src/
├── main/java/ci/hardwork/chatai/
│   ├── core/
│   │   ├── models/          # Entités JPA (Conversation, Message, UserSession)
│   │   ├── repository/      # Repositories Spring Data
│   │   ├── service/         # Services métier
│   │   ├── mapper/          # Mappers MapStruct
│   │   └── dto/            # Data Transfer Objects
│   ├── web/
│   │   └── controller/     # Controllers REST
│   ├── security/           # Configuration JWT et sécurité
│   ├── config/            # Configuration Spring
│   └── exception/         # Gestion des erreurs
└── test/                  # Tests unitaires et d'intégration
```

## 🛠️ Technologies

### Backend
- **Java 17** - Langage principal
- **Spring Boot 3.5.4** - Framework principal
- **Spring AI** - Intégration IA avec Ollama
- **Spring Security** - Authentification et autorisation
- **Spring Data JPA** - Persistance des données
- **PostgreSQL** - Base de données principale
- **MapStruct** - Mapping objet-à-objet
- **Caffeine** - Cache en mémoire

### DevOps
- **Docker** & **Docker Compose** - Conteneurisation
- **Maven** - Gestion des dépendances
- **JUnit 5** & **Mockito** - Tests
- **Swagger/OpenAPI** - Documentation API

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Service auth-service en cours d'exécution

### Installation avec Docker (Recommandé)

1. **Cloner le projet**
```bash
git clone <repository-url>
cd ChatAI
```

2. **Démarrer avec Docker Compose**
```bash
docker-compose up -d
```

Ceci démarre:
- ChatAI service sur le port 8081
- PostgreSQL sur le port 5433
- Ollama sur le port 11434

3. **Initialiser Ollama avec le modèle llama3**
```bash
docker exec -it chatai-ollama ollama pull llama3
```

### Installation Manuelle

1. **Configuration de la base de données**
```sql
CREATE DATABASE chatai_db;
CREATE USER chatai_user WITH PASSWORD 'chatai_password';
GRANT ALL PRIVILEGES ON DATABASE chatai_db TO chatai_user;
```

2. **Configuration des variables d'environnement**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/chatai_db
export SPRING_DATASOURCE_USERNAME=chatai_user
export SPRING_DATASOURCE_PASSWORD=chatai_password
export APP_AUTH_SERVICE_URL=http://localhost:8080
export APP_AI_OLLAMA_BASE_URL=http://localhost:11434
```

3. **Démarrer l'application**
```bash
mvn spring-boot:run
```

## 📚 API Documentation

L'API est documentée avec Swagger et accessible à:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

### Endpoints Principaux

#### Chat
- `POST /chat/message` - Envoyer un message
- `POST /chat/message/stream` - Conversation en streaming
- `POST /chat/generate-title` - Générer un titre
- `GET /chat/models/{modelName}/check` - Vérifier un modèle

#### Conversations  
- `GET /conversations` - Lister les conversations
- `POST /conversations` - Créer une conversation
- `GET /conversations/{id}` - Obtenir une conversation
- `PUT /conversations/{id}` - Mettre à jour une conversation
- `DELETE /conversations/{id}` - Supprimer une conversation
- `GET /conversations/search` - Rechercher des conversations

## 🔧 Configuration

### Variables d'Environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `APP_AUTH_SERVICE_URL` | URL du service d'authentification | `http://localhost:8080` |
| `APP_AI_OLLAMA_BASE_URL` | URL de base d'Ollama | `http://localhost:11434` |
| `APP_AI_OLLAMA_MODEL` | Modèle IA par défaut | `llama3` |
| `APP_RATE_LIMIT_REQUESTS_PER_MINUTE` | Limite de requêtes par minute | `60` |
| `APP_RATE_LIMIT_BURST_CAPACITY` | Capacité de burst | `10` |

### Configuration Spring (application.yml)

```yaml
spring:
  ai:
    ollama:
      base-url: ${APP_AI_OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        options:
          model: ${APP_AI_OLLAMA_MODEL:llama3}
          temperature: 0.7
          max-tokens: 2000

app:
  auth-service:
    url: ${APP_AUTH_SERVICE_URL:http://localhost:8080}
  rate-limit:
    requests-per-minute: ${APP_RATE_LIMIT_REQUESTS_PER_MINUTE:60}
    burst-capacity: ${APP_RATE_LIMIT_BURST_CAPACITY:10}
```

## 🧪 Tests

### Exécuter tous les tests
```bash
mvn test
```

### Tests spécifiques
```bash
mvn test -Dtest=ChatServiceTest
mvn test -Dtest=ConversationServiceTest
```

### Couverture des tests
- **Services**: Tests unitaires complets avec mocks
- **Repositories**: Tests d'intégration avec base de données embarquée
- **Controllers**: Tests avec MockMvc

## 🔒 Sécurité

### Authentification
- **JWT Tokens**: Validation via appels REST au service auth-service
- **Rôles**: Support des rôles PATIENT, DOCTOR, ADMIN
- **Sessions**: Gestion des sessions utilisateur avec tracking

### Rate Limiting
- **Par minute**: Limite configurable de requêtes par utilisateur
- **Burst protection**: Protection contre les pics de trafic
- **Token tracking**: Surveillance de l'utilisation des tokens IA

## 📊 Monitoring & Observabilité

### Health Checks
- `GET /actuator/health` - Status de l'application
- `GET /actuator/info` - Informations sur l'application

### Métriques
- Métriques Micrometer intégrées
- Monitoring des appels IA
- Tracking des performances

## 🐳 Docker

### Build Image
```bash
docker build -t chatai-service .
```

### Variables d'environnement Docker
Voir `docker-compose.yml` pour la configuration complète.

## 📈 Performance

### Optimisations
- **Cache Caffeine** pour les titres générés et modèles
- **Connection pooling** pour la base de données
- **Lazy loading** pour les relations JPA
- **Pagination** pour les listes importantes

### Recommandations de Production
- Configurer un reverse proxy (nginx)
- Utiliser une base Redis pour le cache distribué
- Mettre en place un monitoring avec Prometheus/Grafana
- Configurer des logs centralisés (ELK Stack)

## 🤝 Intégration avec d'autres Services

### Auth Service
- Validation des tokens JWT via REST API
- Récupération des informations utilisateur
- Gestion des rôles et permissions

### Ollama
- Intégration native via Spring AI
- Support de multiple modèles
- Gestion des erreurs et retry automatique

## 📝 Logs

Les logs sont configurés en français et incluent:
- Informations de sessions utilisateur
- Tracking des conversations
- Métriques de performance IA
- Erreurs détaillées avec contexte

## 🔄 Développement

### Ajouter un nouveau modèle IA
1. Configurer le modèle dans Ollama
2. Ajouter la configuration dans `application.yml`
3. Tester via l'endpoint `/chat/models/{modelName}/check`

### Développement local
```bash
# Démarrer uniquement les services externes
docker-compose up -d postgres ollama

# Démarrer l'application en mode développement
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 📋 TODO / Améliorations Futures

- [ ] Interface WebSocket pour chat temps réel
- [ ] Support de fichiers joints (images, documents)
- [ ] Système de plugins pour étendre les capacités IA
- [ ] Dashboard administrateur pour monitoring
- [ ] API GraphQL en complément de REST
- [ ] Support multi-langues complet

## 🐛 Troubleshooting

### Problèmes courants

**Erreur de connexion Ollama**
```bash
# Vérifier qu'Ollama est démarré
curl http://localhost:11434/api/tags

# Télécharger le modèle si nécessaire
docker exec -it chatai-ollama ollama pull llama3
```

**Erreur de base de données**
```bash
# Vérifier la connexion PostgreSQL
docker exec -it chatai-postgres psql -U chatai_user -d chatai_db
```

**Tests qui échouent**
- S'assurer que les ports ne sont pas occupés
- Vérifier que Docker est démarré
- Nettoyer les données de test : `mvn clean test`

---

## 🏆 Qualité du Code

- **Architecture**: Clean Architecture avec séparation des responsabilités
- **SOLID**: Principes SOLID respectés
- **DRY**: Code sans duplication
- **Tests**: Couverture de tests élevée
- **Documentation**: Code auto-documenté et Javadoc
- **Standards**: Respect des conventions Java/Spring
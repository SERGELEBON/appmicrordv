# Configuration Ollama3 pour ChatAI

## Étapes d'installation et configuration d'Ollama

### 1. Installation d'Ollama

#### Sur Linux/Ubuntu:
```bash
# Installer Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Vérifier l'installation
ollama --version
```

#### Avec Docker (Recommandé):
```bash
# Démarrer Ollama avec Docker
docker run -d \
  --name ollama \
  -p 11434:11434 \
  -v ollama:/root/.ollama \
  ollama/ollama

# Vérifier que le conteneur fonctionne
docker ps | grep ollama
```

### 2. Télécharger le Modèle Llama3

```bash
# Si Ollama est installé localement
ollama pull llama3

# Si Ollama est dans Docker
docker exec -it ollama ollama pull llama3

# Vérifier les modèles disponibles
ollama list
# ou avec Docker:
docker exec -it ollama ollama list
```

### 3. Tester la Connexion Ollama

```bash
# Test basique
curl http://localhost:11434/api/tags

# Test avec un prompt
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3",
    "prompt": "Hello, how are you?",
    "stream": false
  }'
```

### 4. Configuration Application ChatAI

#### Dans `application.yml`:
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3
          temperature: 0.7
          max-tokens: 2000

app:
  ai:
    ollama:
      base-url: http://localhost:11434
      model: llama3
```

#### Variables d'environnement:
```bash
export APP_AI_OLLAMA_BASE_URL=http://localhost:11434
export APP_AI_OLLAMA_MODEL=llama3
export SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
```

### 5. Configuration Docker Compose

Le `docker-compose.yml` inclut déjà la configuration Ollama:

```yaml
services:
  ollama:
    image: ollama/ollama:latest
    container_name: chatai-ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    restart: unless-stopped
    networks:
      - chatai-network
    environment:
      - OLLAMA_HOST=0.0.0.0
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
```

### 6. Démarrage Complet avec Docker

```bash
# Démarrer tous les services
docker-compose up -d

# Télécharger le modèle llama3
docker exec -it chatai-ollama ollama pull llama3

# Vérifier les logs
docker-compose logs -f ollama
docker-compose logs -f chatai-app
```

### 7. Résolution des Problèmes Courants

#### Problème: "Connection refused to localhost:11434"
**Solution:**
```bash
# Vérifier qu'Ollama fonctionne
curl http://localhost:11434/api/tags

# Si Docker, vérifier le réseau
docker network ls
docker inspect chatai-network
```

#### Problème: "Model llama3 not found"
**Solution:**
```bash
# Télécharger le modèle
docker exec -it chatai-ollama ollama pull llama3

# Lister les modèles disponibles
docker exec -it chatai-ollama ollama list
```

#### Problème: "Timeout waiting for Ollama response"
**Solution:**
```bash
# Augmenter les timeouts dans application.yml
spring:
  ai:
    ollama:
      chat:
        options:
          timeout: 60s
```

### 8. Test Fonctionnel Complet

```bash
# 1. Vérifier qu'Ollama fonctionne
curl http://localhost:11434/api/tags

# 2. Tester un prompt simple
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3",
    "prompt": "Say hello in French",
    "stream": false
  }'

# 3. Vérifier l'application ChatAI
curl http://localhost:8081/actuator/health

# 4. Tester l'endpoint de vérification du modèle
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8081/chat/models/llama3/check
```

### 9. Monitoring et Logs

```bash
# Logs Ollama
docker logs chatai-ollama

# Logs ChatAI
docker logs chatai-service

# Logs en temps réel
docker-compose logs -f
```

### 10. Configuration Production

#### Ressources recommandées:
- **CPU**: 4+ cores
- **RAM**: 8GB+ (16GB recommandé pour llama3)
- **Stockage**: 10GB+ pour les modèles

#### Variables d'environnement production:
```bash
# Ollama
OLLAMA_HOST=0.0.0.0
OLLAMA_MODELS=/data/ollama/models

# ChatAI
APP_AI_OLLAMA_BASE_URL=http://ollama:11434
APP_AI_OLLAMA_MODEL=llama3
SPRING_AI_OLLAMA_TIMEOUT=120s
```

## ⚠️ Notes Importantes

1. **Premier démarrage**: Le téléchargement de llama3 peut prendre 10-15 minutes
2. **Ressources**: Llama3 nécessite au minimum 8GB de RAM
3. **Réseau Docker**: Les services doivent être sur le même réseau Docker
4. **Sécurité**: En production, sécuriser l'accès à Ollama (port 11434)

## 🚀 Commandes de Démarrage Rapide

```bash
# Tout démarrer d'un coup
cd /home/serge/Téléchargements/microtest/ChatAI
docker-compose up -d
sleep 30  # Attendre qu'Ollama démarre
docker exec -it chatai-ollama ollama pull llama3
docker-compose restart chatai-app
```
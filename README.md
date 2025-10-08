# Assistant - Banking Chatbot

## English

### Overview
Assistant is a Spring Boot application that acts as a banking chatbot. It processes user queries, detects intents (such as checking balance or weather), and responds accordingly. The app uses the Strategy pattern for intent processing and persists conversation history in an H2 database.

### Architecture
- **Spring Boot** REST API
- **Strategy Pattern** for intent detection
- **H2 Database** for conversation history
- **External API** integration (Weather)

### Main Endpoints
- `POST /api/v1/assistant/query` : Receives a user query and returns a processed response.

#### Request Example (Weather)
```json
{
  "userId": "1234",
  "userQuery": "What is the weather in city:Buenos Aires?"
}
```

#### Response Example
```json
{
  "conversationId": "...",
  "userId": "1234",
  "processedIntent": "INTENT_WEATHER_QUERY",
  "assistantResponse": "The current weather in Buenos Aires is 25.5°C.",
  "serviceStatus": "OK",
  "processedTimestamp": "..."
}
```

### How to Run
1. Build: `mvn clean package`
2. Run: `java -jar target/assistant-0.0.1-SNAPSHOT.jar`
3. Docker: `docker build -t assistant . && docker run -p 8080:8080 assistant`

---

# Authentication Example

To use the Assistant API, you must first authenticate and obtain a JWT token.

## 1. Login (Obtain Token)

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user",
    "password": "password"
}'
```

The response will include a JWT token. You must use this token in the Authorization header for subsequent requests.

## 2. Query Example (Authenticated)

```bash
curl --location 'http://localhost:8080/api/v1/assistant/query' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <token obtenido>' \
--data '{
    "userId": "a1b2c3d4-e5f6-4321-7890-000000000001",
    "userQuery": "quisiera saber el clima ciudad: Buenos Aires."
}'
```

Replace `<token obtenido>` with the token received from the login endpoint.

---

## Español

### Descripción
Assistant es una aplicación Spring Boot que funciona como chatbot bancario. Procesa consultas de usuarios, detecta intenciones (como consultar saldo o clima) y responde en consecuencia. Utiliza el patrón Strategy para el procesamiento y guarda el historial en una base H2.

### Arquitectura
- **API REST** con Spring Boot
- **Patrón Strategy** para detección de intención
- **Base H2** para historial de conversaciones
- **Integración con API externa** (Clima)

### Endpoints principales
- `POST /api/v1/assistant/query` : Recibe una consulta y devuelve la respuesta procesada.

#### Ejemplo de petición (Clima)
```json
{
  "userId": "1234",
  "userQuery": "¿Qué temperatura hace ciudad:Buenos Aires?"
}
```

#### Ejemplo de respuesta
```json
{
  "conversationId": "...",
  "userId": "1234",
  "processedIntent": "INTENT_WEATHER_QUERY",
  "assistantResponse": "El clima actual en Buenos Aires es de 25.5°C.",
  "serviceStatus": "OK",
  "processedTimestamp": "..."
}
```

### Cómo ejecutar
1. Compilar: `mvn clean package`
2. Ejecutar: `java -jar target/assistant-0.0.1-SNAPSHOT.jar`
3. Docker: `docker build -t assistant . && docker run -p 8080:8080 assistant`

---

# Ejemplo de Autenticación

Para usar la API de Assistant, primero debes autenticarte y obtener un token JWT.

## 1. Login (Obtener Token)

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user",
    "password": "password"
}'
```

La respuesta incluirá un token JWT. Debes usar este token en el header Authorization para las siguientes consultas.

## 2. Ejemplo de Consulta (Autenticado)

```bash
curl --location 'http://localhost:8080/api/v1/assistant/query' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <token obtenido>' \
--data '{
    "userId": "a1b2c3d4-e5f6-4321-7890-000000000001",
    "userQuery": "quisiera saber el clima ciudad: Buenos Aires."
}'
```

Reemplaza `<token obtenido>` por el token recibido en el login.

---

## Component Diagram

```
+-------------------+         +-------------------+         +-------------------+
|   Client (curl)   |  --->   |  API REST Spring  |  --->   |   IntentProcessing|
+-------------------+         +-------------------+         +-------------------+
        |                           |                              |
        |  /api/v1/auth/login       |                              |
        |  /api/v1/assistant/query  |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Security JWT            |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   AssistantController     |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   IntentProcessingService |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Strategies (Strategy)   |                              |
        |   | Balance | Weather |   |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   External API (Weather)  |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Persistence H2 DB       |                              |
        +---------------------------+------------------------------+
```

**Quick Explanation:**
- The client authenticates and queries using JWT.
- The controller receives the query and processes it synchronously.
- The service detects the intent (strategies), queries the external API if necessary, and saves the history in H2.

---

## Diagrama de Componentes

```
+-------------------+         +-------------------+         +-------------------+
|   Cliente (curl)  |  --->   |  API REST Spring  |  --->   | Procesamiento     |
+-------------------+         +-------------------+         +-------------------+
        |                           |                              |
        |  /api/v1/auth/login       |                              |
        |  /api/v1/assistant/query  |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Seguridad JWT           |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Controlador Assistant   |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   IntentProcessingService |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Estrategias (Strategy)  |                              |
        |   | Balance | Clima |     |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   API Externa (Clima)     |                              |
        |         |                 |                              |
        |         v                 |                              |
        |   Persistencia H2 DB      |                              |
        +---------------------------+------------------------------+
```

**Explicación rápida:**
- El cliente se autentica y consulta usando JWT.
- El controlador recibe la consulta y la procesa de forma síncrona.
- El servicio detecta la intención (estrategias), consulta la API externa si es necesario y guarda el historial en H2.

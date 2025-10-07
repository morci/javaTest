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

#### Request Example
```json
{
  "userId": "1234",
  "userQuery": "What is my balance?"
}
```

#### Response Example
```json
{
  "conversationId": "...",
  "userId": "1234",
  "processedIntent": "INTENT_CHECK_BALANCE",
  "assistantResponse": "To check your balance, I need to verify your identity.",
  "serviceStatus": "OK",
  "processedTimestamp": "..."
}
```

### How to Run
1. Build: `mvn clean package`
2. Run: `java -jar target/assistant-0.0.1-SNAPSHOT.jar`
3. Docker: `docker build -t assistant . && docker run -p 8080:8080 assistant`

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

#### Ejemplo de petición
```json
{
  "userId": "1234",
  "userQuery": "¿Cuál es mi saldo?"
}
```

#### Ejemplo de respuesta
```json
{
  "conversationId": "...",
  "userId": "1234",
  "processedIntent": "INTENT_CHECK_BALANCE",
  "assistantResponse": "Para consultar su saldo, necesitaría verificar su identidad.",
  "serviceStatus": "OK",
  "processedTimestamp": "..."
}
```

### Cómo ejecutar
1. Compilar: `mvn clean package`
2. Ejecutar: `java -jar target/assistant-0.0.1-SNAPSHOT.jar`
3. Docker: `docker build -t assistant . && docker run -p 8080:8080 assistant`


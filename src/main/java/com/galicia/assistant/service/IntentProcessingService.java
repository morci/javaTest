package com.galicia.assistant.service;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.entity.ConversationEntry;
import com.galicia.assistant.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException; // Para manejar errores HTTP del cliente

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class IntentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(IntentProcessingService.class);

    private final ConversationRepository conversationRepository;
    private final WeatherApiClient weatherApiClient;

    // Constructor que inyecta el Repositorio y el Cliente de API
    public IntentProcessingService(ConversationRepository conversationRepository, WeatherApiClient weatherApiClient) {
        this.conversationRepository = conversationRepository;
        this.weatherApiClient = weatherApiClient;
    }

    /**
     * Método principal llamado por el Controller para procesar la consulta,
     * identificar la intención y generar la respuesta.
     * * @param request La consulta del usuario.
     * @return QueryResponse con la respuesta del asistente.
     */
    public QueryResponse processQuery(QueryRequest request) {

        // 1. Inicialización de Variables
        String conversationId = UUID.randomUUID().toString();
        // Se declaran e inicializan aquí para que tengan ámbito en todo el método
        String intent;
        String responseText;
        String status = "OK";

        String userQuery = request.getUserQuery().toLowerCase();

        if (userQuery.contains("clima") || userQuery.contains("tiempo") || userQuery.contains("temperatura")) {

            String city = "Buenos Aires"; // Extracción simple de la ciudad

            try {
                // 2. Lógica de Integración Real: Llamada a la API externa
                // El resultado es un Map anidado, lo parseamos para obtener la temperatura
                Map<String, Object> apiResponse = weatherApiClient.getWeatherForCity(city);
                LinkedHashMap<String, Object> weatherData = (LinkedHashMap<String, Object>) apiResponse.get("main");
                Double temp = (Double) weatherData.get("temp");

                intent = "INTENT_WEATHER_QUERY";
                responseText = String.format("El clima actual en %s es de %.1f°C.", city, temp);

            } catch (HttpClientErrorException e) {
                // 3. Manejo de Errores Específicos (ej. ciudad no encontrada, clave API inválida)
                log.error("Error HTTP al llamar a la API de clima: {}", e.getStatusCode());
                intent = "INTENT_WEATHER_QUERY_ERROR";
                responseText = "No pude encontrar el clima para esa ciudad o la clave API es inválida.";
                status = "ERROR_API_CLIENT";
            } catch (Exception e) {
                // 4. Manejo de Errores Generales (ej. fallo de red, JSON inesperado)
                log.error("Fallo inesperado al procesar el clima: {}", e.getMessage(), e);
                intent = "INTENT_GENERAL_ERROR";
                responseText = "Disculpe, hubo un problema técnico al consultar el clima. Intente más tarde.";
                status = "ERROR_INTERNAL";
            }

        } else if (userQuery.contains("saldo") || userQuery.contains("cuánto tengo")) {

            intent = "INTENT_CHECK_BALANCE";
            responseText = "Para consultar su saldo, necesitaría verificar su identidad. (Lógica de API interna simulada).";

        } else {

            intent = "INTENT_UNKNOWN";
            responseText = "Disculpe, no entiendo la intención. Por favor, reformule su consulta.";
        }

        // 5. Crear Objeto de Respuesta
        QueryResponse response = new QueryResponse(
                request.getUserId(),
                intent,
                responseText,
                status
        );
        response.setConversationId(conversationId);

        // 6. Persistencia (Guardar Historial)
        saveConversationHistory(request, response);

        log.info("Procesamiento completado y respuesta generada para ConvID: {}", conversationId);

        return response; // Devuelve la respuesta al Controller
    }

    /**
     * Guarda la entrada de conversación en la base de datos (Persistencia).
     */
    private void saveConversationHistory(QueryRequest request, QueryResponse response) {
        ConversationEntry entry = new ConversationEntry(
                response.getConversationId(),
                request.getUserId(),
                request.getUserQuery(),
                response.getProcessedIntent(),
                response.getAssistantResponse(),
                request.getTimestamp(),
                response.getServiceStatus()
        );
        conversationRepository.save(entry);
        log.info("Historial guardado en H2 para ConvID: {}", entry.getConversationId());
    }
}

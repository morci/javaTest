package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.service.WeatherApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WeatherIntentStrategy implements IntentStrategy {

    private static final Logger log = LoggerFactory.getLogger(WeatherIntentStrategy.class);
    private final WeatherApiClient weatherApiClient;

    public WeatherIntentStrategy(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    // ------------------ Contrato IntentStrategy ------------------

    @Override
    public boolean matches(String userQuery) {
        String lowerQuery = userQuery.toLowerCase();
        return lowerQuery.contains("clima") || lowerQuery.contains("tiempo") || lowerQuery.contains("temperatura");
    }

    @Override
    public QueryResponse process(QueryRequest request, String conversationId) {
        String userQuery = request.getUserQuery(); // No pasar a minúsculas
        String city = extractCityFromQuery(userQuery); // Extrae la ciudad en formato original
        String intent;
        String responseText;
        String status = "OK";

        if (city == null) {
            intent = "INTENT_WEATHER_MISSING_CITY";
            responseText = "Para saber el clima, necesito la ciudad. Por favor, escriba: ciudad:[Nombre de la ciudad].";
            status = "ERROR_BAD_REQUEST";
        } else {
            try {
                Map<String, Object> apiResponse = weatherApiClient.getWeatherForCity(city);
                LinkedHashMap<String, Object> weatherData = (LinkedHashMap<String, Object>) apiResponse.get("main");
                if (weatherData == null || weatherData.get("temp") == null) {
                    intent = "INTENT_GENERAL_ERROR";
                    responseText = "Disculpe, hubo un problema técnico al consultar el clima. Intente más tarde.";
                    status = "ERROR_INTERNAL";
                } else {
                    Double temp = (Double) weatherData.get("temp");
                    intent = "INTENT_WEATHER_QUERY";
                    responseText = String.format("El clima actual en %s es de %.1f°C.", city, temp);
                }
            } catch (HttpClientErrorException e) {
                log.error("Error HTTP en API de clima para {}: {}", city, e.getStatusCode());
                intent = "INTENT_WEATHER_QUERY_ERROR";
                responseText = "No pude encontrar el clima para esa ciudad. Verifique la clave API o el nombre.";
                status = "ERROR_API_CLIENT";
            } catch (Exception e) {
                log.error("Fallo inesperado al procesar el clima: {}", e.getMessage(), e);
                intent = "INTENT_GENERAL_ERROR";
                responseText = "Disculpe, hubo un problema técnico al consultar el clima. Intente más tarde.";
                status = "ERROR_INTERNAL";
            }
        }

        QueryResponse response = new QueryResponse(request.getUserId(), intent, responseText, status);
        response.setConversationId(conversationId);
        return response;
    }

    // ------------------ Métodos Auxiliares ------------------

    /**
     * Extrae el nombre de la ciudad usando el patrón "ciudad:[nombre]" (hasta punto, signo de interrogación, o fin de línea).
     */
    private String extractCityFromQuery(String query) {
        Pattern pattern = Pattern.compile("ciudad:\\s*([\\p{L} .'-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String city = matcher.group(1).trim();
            // Elimina posibles puntos o signos de interrogación al final
            city = city.replaceAll("[.¿?]+$", "").trim();
            return city.isEmpty() ? null : city;
        }
        return null;
    }
}

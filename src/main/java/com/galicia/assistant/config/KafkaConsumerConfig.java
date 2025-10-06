/*
package com.galicia.assistant.config;

import com.galicia.assistant.dto.QueryRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, QueryRequest> consumerFactory() {

        // 1. Configuración básica de Kafka
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        // 2. Configuración del Deserializador de Clave (String)
        // Usamos ErrorHandlingDeserializer para envolver el StringDeserializer
        ErrorHandlingDeserializer<String> keyDeserializer =
                new ErrorHandlingDeserializer<>(new StringDeserializer());


        // 3. Configuración del Deserializador de Valor (JSON a QueryRequest)
        // a) Configuración del JsonDeserializer
        JsonDeserializer<QueryRequest> jsonDeserializer = new JsonDeserializer<>(QueryRequest.class);
        jsonDeserializer.setRemoveTypeHeaders(false);
        jsonDeserializer.addTrustedPackages("com.galicia.assistant.dto.*", "java.time.*");
        jsonDeserializer.setUseTypeHeaders(false);

        // b) Usamos ErrorHandlingDeserializer para envolver el JsonDeserializer.
        // Esto captura excepciones de deserialización y evita que la aplicación se caiga.
        ErrorHandlingDeserializer<QueryRequest> valueDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);


        return new DefaultKafkaConsumerFactory<>(
                props,
                keyDeserializer,
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, QueryRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QueryRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Es buena práctica configurar un error handler para el contenedor
        // factory.setErrorHandler(...); 

        return factory;
    }
}
*/

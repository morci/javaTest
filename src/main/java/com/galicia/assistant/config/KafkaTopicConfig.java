/*
package com.galicia.assistant.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_QUERIES_TOPIC = "user-queries-in";
    public static final String ASSISTANT_RESPONSES_TOPIC = "assistant-responses-out";

    // 🌟 INYECTAMOS LA PROPIEDAD DE BOOTSTRAP-SERVERS (AHORA CON EL VALOR EMBEBIDO)
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // 🌟 BEAN DE ADMINISTRACIÓN DE KAFKA (CRÍTICO PARA CONECTAR EL ADMIN CLIENT)
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        // Aseguramos que KafkaAdmin use la propiedad inyectada (que será la embebida)
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // El resto de la creación de tópicos sigue igual, usando TopicBuilder
    @Bean
    public NewTopic userQueriesTopic() {
        return TopicBuilder.name(USER_QUERIES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic assistantResponsesTopic() {
        return TopicBuilder.name(ASSISTANT_RESPONSES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
*/

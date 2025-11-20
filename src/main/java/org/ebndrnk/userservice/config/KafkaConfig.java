package org.ebndrnk.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ebndrnk.userservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.userservice.kafka.dto.UserProfileCreationFailedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for topics, producers, consumers, and listener container factories.
 */
@Configuration
public class KafkaConfig {

    @Value("${environment.kafka.address}")
    private String bootstrapServers;

    /**
     * Defines a Kafka topic for failed user profile creation events.
     *
     * @return a new topic named "user.profile.creation.failed"
     */
    @Bean
    public NewTopic userProfileFailedTopic() {
        return TopicBuilder.name("user.profile.creation.failed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Configures the Kafka producer factory for sending {@link UserProfileCreationFailedEvent}.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, UserProfileCreationFailedEvent> userProfileFailedProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // ensures exactly-once delivery
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Provides the KafkaTemplate used to publish {@link UserProfileCreationFailedEvent}.
     *
     * @return configured KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, UserProfileCreationFailedEvent> userProfileFailedKafkaTemplate() {
        KafkaTemplate<String, UserProfileCreationFailedEvent> template =
                new KafkaTemplate<>(userProfileFailedProducerFactory());
        template.setObservationEnabled(true); // enables tracing/metrics
        return template;
    }

    /**
     * Configures the Kafka consumer factory for receiving {@link UserCreatedEvent}.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, UserCreatedEvent> userCreatedEventConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // manual commit
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minutes

        JsonDeserializer<UserCreatedEvent> deserializer = new JsonDeserializer<>(UserCreatedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * Defines the error handler for Kafka consumers.
     *
     * @return a DefaultErrorHandler with fixed backoff
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(
                new FixedBackOff(1000L, 3L) // retry 3 times with 1 second delay
        );
    }

    /**
     * Configures the Kafka listener container factory for {@link UserCreatedEvent}.
     *
     * @return the ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent>
    userCreatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userCreatedEventConsumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}

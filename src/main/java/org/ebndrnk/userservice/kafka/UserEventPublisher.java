package org.ebndrnk.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.kafka.dto.UserProfileCreationFailedEvent;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes user-related events to Kafka topics.
 * <p>
 * Specifically responsible for publishing events when user profile creation fails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserProfileCreationFailedEvent> kafkaTemplate;

    private static final String TOPIC = "user.profile.creation.failed";

    /**
     * Publishes a {@link UserProfileCreationFailedEvent} to the designated Kafka topic.
     *
     * @param event the event containing failure details
     * @throws KafkaException if the event cannot be sent
     */
    public void publishUserProfileCreationFailed(UserProfileCreationFailedEvent event) {
        log.warn("Publishing User failure to topic {}: {}", TOPIC, event);
        try {
            kafkaTemplate.send(TOPIC, event.email(), event);
        } catch (Exception e) {
            log.error("Failed to send event for email: {}", event.email());
            throw new KafkaException("Failed to send event for email: " + event.email(), e);
        }
    }
}

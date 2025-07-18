package org.ebndrnk.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.userservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.userservice.kafka.dto.UserProfileCreationFailedEvent;
import org.ebndrnk.userservice.mapper.UserMapper;
import org.ebndrnk.userservice.service.user.UserService;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka listener that processes user creation events.
 * <p>
 * Listens to the "user.created" topic and creates a user in the system.
 * In case of errors, it publishes a failure event to notify other services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreatedListener {

    private final UserService userService;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    /**
     * Handles incoming user creation events from Kafka.
     * If the user is successfully created, the message is acknowledged.
     * On failure, a failure event is published and a {@link KafkaException} is thrown.
     *
     * @param event          the incoming {@link UserCreatedEvent} with user details
     * @param acknowledgment manual acknowledgment object for committing the offset
     */
    @KafkaListener(
            topics = "user.created",
            groupId = "user-service-group",
            containerFactory = "userCreatedKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserCreated(UserCreatedEvent event, Acknowledgment acknowledgment) {
        final String email = event.email();

        try {
            processUserCreation(event);
            acknowledgment.acknowledge();
            log.info("Successfully processed user creation for email: {}", email);
        } catch (Exception e) {
            sendFailureNotification(email, e.getMessage());
            throw new KafkaException("Failed to process user creation for email: " + email, e);
        }
    }

    /**
     * Attempts to create a user from the provided event.
     * Throws a {@link DuplicateEmailException} if the user already exists.
     *
     * @param event the user creation event containing user data
     */
    private void processUserCreation(UserCreatedEvent event) {
        String email = event.email();

        if (userService.isExistByEmail(email)) {
            throw new DuplicateEmailException("User with email: " + email + " already exists");
        }

        userService.createUser(userMapper.eventToRequest(event));
    }

    /**
     * Publishes a user profile creation failure event to Kafka.
     * Wraps any exception in a {@link KafkaException}.
     *
     * @param email  the email of the user whose profile creation failed
     * @param reason the reason for failure
     */
    private void sendFailureNotification(String email, String reason) {
        try {
            userEventPublisher.publishUserProfileCreationFailed(
                    new UserProfileCreationFailedEvent(email, reason)
            );
        } catch (Exception ex) {
            log.error("Failed to send failure notification for email: {}", email, ex);
            throw new KafkaException("Failed to send failure notification for email: " + email, ex);
        }
    }
}

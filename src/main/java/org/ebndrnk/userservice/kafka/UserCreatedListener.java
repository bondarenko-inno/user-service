package org.ebndrnk.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.userservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.userservice.kafka.dto.UserProfileCreationFailedEvent;
import org.ebndrnk.userservice.mapper.UserMapper;
import org.ebndrnk.userservice.service.user.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreatedListener {

    private final UserService userService;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

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
            throw new RuntimeException("Failed to process user creation for email: " + email, e);
        }
    }

    private void processUserCreation(UserCreatedEvent event) {
        String email = event.email();

        if(userService.isExistByEmail(email)) {
            throw new DuplicateEmailException("User with email: " + email + " already exists");
        }

        userService.createUser(userMapper.eventToRequest(event));
    }


    private void sendFailureNotification(String email, String reason) {
        try {
            userEventPublisher.publishUserProfileCreationFailed(
                    new UserProfileCreationFailedEvent(email, reason)
            );
        } catch (Exception ex) {
            log.error("Failed to send failure notification for email: {}", email, ex);
        }
    }
}
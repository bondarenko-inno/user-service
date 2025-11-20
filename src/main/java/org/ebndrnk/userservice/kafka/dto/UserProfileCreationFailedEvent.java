package org.ebndrnk.userservice.kafka.dto;

public record UserProfileCreationFailedEvent(
         String email,
         String reason
) {}

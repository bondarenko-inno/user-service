package org.ebndrnk.userservice.kafka.dto;

import java.time.LocalDateTime;

public record UserCreatedEvent(
        String email,
        LocalDateTime birthDate,
        String name,
        String surname
){
}

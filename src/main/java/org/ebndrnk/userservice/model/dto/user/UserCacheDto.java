package org.ebndrnk.userservice.model.dto.user;


import java.time.LocalDateTime;

public record UserCacheDto(
        Long id,
        String name,
        String surname,
        String email,
        LocalDateTime birthDate
) {}

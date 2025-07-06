package org.ebndrnk.userservice.model.dto.user;


import java.time.LocalDateTime;

/**
 * Cache DTO representing user information stored in cache.
 *
 * @param id        unique identifier of the user
 * @param name      user's first name
 * @param surname   user's last name
 * @param email     user's email address
 * @param birthDate user's birthdate and time
 */
public record UserCacheDto(
        Long id,
        String name,
        String surname,
        String email,
        LocalDateTime birthDate
) {}

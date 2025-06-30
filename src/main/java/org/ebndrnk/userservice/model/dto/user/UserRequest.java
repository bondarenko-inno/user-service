package org.ebndrnk.userservice.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UserRequest(
        @NotNull
        @Size(min = 2, max = 50)
        String name,

        @NotNull
        @Size(min = 2, max = 50)
        String surname,

        @NotNull
        @Email
        @Size(min = 4, max = 50)
        String email,

        @NotNull
        LocalDateTime birthDate
) {
}

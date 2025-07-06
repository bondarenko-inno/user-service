package org.ebndrnk.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for creating or updating a user.
 *
 * @param name      user's first name, between 2 and 50 characters
 * @param surname   user's surname, between 2 and 50 characters
 * @param email     user's email address, valid email format, between 4 and 50 characters
 * @param birthDate user's date of birth in ISO 8601 format
 */
@Schema(description = "Request for creating or updating a user.")
public record UserRequest(

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's first name.", example = "John")
        String name,

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's surname.", example = "Doe")
        String surname,

        @NotNull
        @Email
        @Size(min = 4, max = 50)
        @Schema(description = "User's email address.", example = "john.doe@example.com")
        String email,

        @NotNull
        @Schema(description = "User's date of birth. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "1990-05-15T00:00:00")
        LocalDateTime birthDate

) {
}

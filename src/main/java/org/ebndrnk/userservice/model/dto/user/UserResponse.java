package org.ebndrnk.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing user information.")
public record UserResponse(

        @Schema(description = "Unique identifier of the user.", example = "10")
        Long id,

        @Schema(description = "User's first name.", example = "John")
        String name,

        @Schema(description = "User's surname.", example = "Doe")
        String surname,

        @Schema(description = "User's email address.", example = "john.doe@example.com")
        String email,

        @Schema(description = "User's date of birth. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "1990-05-15T00:00:00")
        LocalDateTime birthDate

) {
}


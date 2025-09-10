package org.ebndrnk.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoForOrder{
        @Schema(description = "Unique identifier of the user.", example = "10")
        private Long id;

        @Schema(description = "User's first name.", example = "John")
        private String name;

        @Schema(description = "User's surname.", example = "Doe")
        private String surname;

        @Schema(description = "User's email address.", example = "john.doe@example.com")
        private String email;

        @Schema(description = "User's date of birth. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "1990-05-15T00:00:00")
        private LocalDateTime birthDate;

        private Boolean isCardAvailable;
}

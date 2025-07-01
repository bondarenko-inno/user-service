package org.ebndrnk.userservice.model.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;


@Schema(description = "Request for creating or updating user card information.")
public record CardInfoRequest(
        @NotNull
        @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
        @Schema(description = "16-digit card number.", example = "1234567812345678")
        String number,

        @NotNull
        @Schema(description = "Expiration date of the card. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "2025-12-31T23:59:59")
        LocalDateTime expirationDate,

        @NotNull
        @Schema(description = "Name of the card holder.", example = "John Doe")
        String holder,

        @NotNull
        @Schema(description = "Identifier of the user who owns the card.", example = "1")
        Long userId
) {
}


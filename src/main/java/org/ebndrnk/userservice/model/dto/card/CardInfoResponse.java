package org.ebndrnk.userservice.model.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing card information.")
public record CardInfoResponse(

        @Schema(description = "Unique identifier of the card.", example = "10")
        Long id,

        @Schema(description = "16-digit card number.", example = "1234567812345678")
        String number,

        @Schema(description = "Name of the card holder.", example = "John Doe")
        String holder,

        @Schema(description = "Expiration date of the card. \n Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000", example = "2025-12-31T23:59:59")
        LocalDateTime expirationDate,

        @Schema(description = "Identifier of the user who owns the card.", example = "1")
        Long userId
) {
}

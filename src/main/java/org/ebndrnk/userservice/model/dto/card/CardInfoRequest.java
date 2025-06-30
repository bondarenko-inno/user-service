package org.ebndrnk.userservice.model.dto.card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;


public record CardInfoRequest(
        @NotNull
        @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
        String number,

        @NotNull
        LocalDateTime expirationDate,

        @NotNull
        String holder,

        @NotNull
        Long userId
) {
}


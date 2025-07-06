package org.ebndrnk.userservice.model.dto.card;

import java.time.LocalDateTime;

/**
 * Cache DTO representing card information stored in cache.
 *
 * @param id             the unique identifier of the card
 * @param number         the card number
 * @param holder         the name of the cardholder
 * @param expirationDate the expiration date of the card
 * @param userId         the identifier of the user owning the card
 */
public record CardInfoCacheDto(Long id,
                               String number,
                               String holder,
                               LocalDateTime expirationDate,
                               Long userId) {
}


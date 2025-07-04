package org.ebndrnk.userservice.model.dto.card;

import java.time.LocalDateTime;

public record CardInfoCacheDto(Long id,
                               String number,
                               String holder,
                               LocalDateTime expirationDate,
                               Long userId
                               ) {
}

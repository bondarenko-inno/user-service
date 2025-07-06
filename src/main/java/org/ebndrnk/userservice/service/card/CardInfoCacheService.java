package org.ebndrnk.userservice.service.card;

import jakarta.transaction.Transactional;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;

import java.util.Optional;

/**
 * Service interface for managing card information cache.
 * <p>
 * Provides methods to retrieve, save, and delete card cache data
 * in a transactional context to ensure consistency.
 */
public interface CardInfoCacheService {

    /**
     * Retrieves a cached card information DTO by its identifier.
     *
     * @param id the unique identifier of the card
     * @return an Optional containing the cached card information if found, or empty otherwise
     */
    Optional<CardInfoCacheDto> findById(Long id);

    /**
     * Saves or updates the card information cache entry.
     *
     * @param cardCachedDto the DTO representing the card information to cache
     */
    void save(CardInfoCacheDto cardCachedDto);

    /**
     * Deletes the cached card information by its identifier.
     *
     * @param id the unique identifier of the card cache to delete
     */
    void deleteById(Long id);
}

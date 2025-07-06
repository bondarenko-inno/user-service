package org.ebndrnk.userservice.repository.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.repository.RedisCrudRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Repository for managing CardInfoCacheDto objects in Redis.
 * Provides basic CRUD operations with a fixed TTL for cached data.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CardInfoRedisRepository implements RedisCrudRepository<CardInfoCacheDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "CARD:";
    private static final Duration TTL = Duration.ofMinutes(20);
    private final ObjectMapper mapper;

    /**
     * Saves the given CardInfoCacheDto object into Redis with a TTL of 20 minutes.
     *
     * @param cardInfoCacheDto the card information to cache
     */
    @Override
    public void save(CardInfoCacheDto cardInfoCacheDto) {
        try {
            redisTemplate.opsForValue().set(PREFIX + cardInfoCacheDto.id(), cardInfoCacheDto, TTL);
        } catch (Exception e) {
            log.error("Failed to save card to Redis: {}", e.getMessage());
        }
    }

    /**
     * Retrieves a CardInfoCacheDto from Redis by its ID.
     * If the value is present, it is converted from the stored JSON back into a DTO.
     *
     * @param id the unique identifier of the card
     * @return an Optional containing the CardInfoCacheDto if found, or empty otherwise
     */
    @Override
    public Optional<CardInfoCacheDto> findById(Long id) {
        try {
            Object object = redisTemplate.opsForValue().get(PREFIX + id);
            CardInfoCacheDto cardInfo = mapper.convertValue(object, CardInfoCacheDto.class);
            return Optional.ofNullable(cardInfo);
        } catch (Exception e) {
            log.error("Failed to read card from Redis: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Deletes the cached card information from Redis by its ID.
     *
     * @param id the unique identifier of the card to delete from cache
     */
    @Override
    public void deleteById(Long id) {
        try {
            redisTemplate.delete(PREFIX + id);
        } catch (Exception e) {
            log.error("Failed to delete card from Redis: {}", e.getMessage());
        }
    }
}

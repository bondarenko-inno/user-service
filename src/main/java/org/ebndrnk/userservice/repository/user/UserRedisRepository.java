package org.ebndrnk.userservice.repository.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.repository.RedisCrudRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Repository responsible for caching UserCacheDto objects in Redis.
 * Provides basic CRUD operations with a time-to-live (TTL) for cached entries.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRedisRepository implements RedisCrudRepository<UserCacheDto> {

    private final RedisTemplate<String, Object> redisTemplate;

    /** Prefix to distinguish user keys in Redis */
    private static final String PREFIX = "USER:";

    /** Time-to-live for cached user data */
    private static final Duration TTL = Duration.ofMinutes(20);

    private final ObjectMapper mapper;

    /**
     * Saves a UserCacheDto to Redis cache with a TTL.
     *
     * @param user the user data to cache
     */
    @Override
    public void save(UserCacheDto user) {
        try {
            redisTemplate.opsForValue().set(PREFIX + user.id(), user, TTL);
            log.debug("Saved user {} to Redis", user.id());
        } catch (Exception e) {
            log.error("Failed to save user to Redis: {}", user.id(), e);
        }
    }

    /**
     * Retrieves a UserCacheDto from Redis by its ID.
     * Deserializes the cached object into a UserCacheDto instance.
     *
     * @param id the unique identifier of the user
     * @return an Optional containing the user if present, or empty otherwise
     */
    @Override
    public Optional<UserCacheDto> findById(Long id) {
        try {
            Object object = redisTemplate.opsForValue().get(PREFIX + id);
            UserCacheDto user = mapper.convertValue(object, UserCacheDto.class);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Failed to read user from Redis: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Deletes the cached user data by user ID from Redis.
     *
     * @param id the unique identifier of the user to remove from cache
     */
    @Override
    public void deleteById(Long id) {
        try {
            redisTemplate.delete(PREFIX + id);
            log.debug("Deleted user {} from Redis", id);
        } catch (Exception e) {
            log.error("Failed to delete user from Redis: {}", id, e);
        }
    }
}

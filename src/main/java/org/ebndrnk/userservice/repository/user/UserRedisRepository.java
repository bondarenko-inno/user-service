package org.ebndrnk.userservice.repository.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.RedisCrudRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRedisRepository implements RedisCrudRepository<UserCacheDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "USER:";
    private static final Duration TTL = Duration.ofMinutes(20);
    private final ObjectMapper mapper;

    @Override
    public void save(UserCacheDto user) {
        try {
            redisTemplate.opsForValue().set(PREFIX + user.id(), user, TTL);
            log.debug("Saved user {} to Redis", user.id());
        } catch (Exception e) {
            log.error("Failed to save user to Redis: {}", user.id(), e);
        }
    }

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

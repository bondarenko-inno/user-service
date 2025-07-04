package org.ebndrnk.userservice.repository.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.repository.RedisCrudRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CardInfoRedisRepository implements RedisCrudRepository<CardInfoCacheDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "CARD:";
    private static final Duration TTL = Duration.ofMinutes(20);
    private final ObjectMapper mapper;

    @Override
    public void save(CardInfoCacheDto cardInfoCacheDto) {
        try {
            redisTemplate.opsForValue().set(PREFIX + cardInfoCacheDto.id(), cardInfoCacheDto, TTL);
        } catch (Exception e) {
            log.error("Failed to save card to Redis: {}", e.getMessage());
        }
    }

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

    @Override
    public void deleteById(Long id) {
        try {
            redisTemplate.delete(PREFIX + id);
        } catch (Exception e) {
            log.error("Failed to delete card from Redis: {}", e.getMessage());
        }
    }
}

package org.ebndrnk.userservice.service.card;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.repository.card.CardInfoRedisRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoCacheService {

    private final CardInfoRedisRepository cardRedisRepository;

    public Optional<CardInfoCacheDto> findById(Long id) {
        try {
            return cardRedisRepository.findById(id);
        } catch (Exception e) {
            log.error("Failed to fetch card from Redis", e);
            return Optional.empty();
        }
    }

    @Transactional
    public void save(CardInfoCacheDto cardCachedDto) {
        try {
            cardRedisRepository.save(cardCachedDto);
        } catch (Exception e) {
            log.error("Failed to save user to Redis", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            cardRedisRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete user from Redis", e);
        }
    }
}

package org.ebndrnk.userservice.service.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.card.CardInfoNotFoundException;
import org.ebndrnk.userservice.exception.card.DuplicateCardNumberException;
import org.ebndrnk.userservice.exception.card.ExpiredCardException;
import org.ebndrnk.userservice.mapper.CardInfoMapper;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserService userService;
    private final CardInfoMapper cardInfoMapper;
    private final CardInfoCacheService cardInfoCacheService;

    @Override
    @Transactional
    public CardInfoResponse createCard(CardInfoRequest request) {
        log.info("Creating card with number: {}", request.number());

        if (cardInfoRepository.findByNumber(request.number()).isPresent()) {
            log.warn("Duplicate card number attempted: {}", request.number());
            throw new DuplicateCardNumberException("Card with number " + request.number() + " already exists");
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to create card with expired date: {}", request.expirationDate());
            throw new ExpiredCardException("Cannot register an expired card");
        }


        userService.getUserById(request.userId());


        CardInfo saved = cardInfoRepository.save(cardInfoMapper.toEntity(request));

        cardInfoCacheService.save(cardInfoMapper.toCacheDto(saved));

        CardInfoResponse response = cardInfoMapper.toDto(saved);
        log.info("Card created with id: {}", response.id());
        return response;
    }

    @Override
    public CardInfoResponse getCardById(Long id) {
        log.info("Fetching card by id: {}", id);

        return cardInfoCacheService.findById(id)
                .map(cardInfoMapper::toDto)
                .or(() -> cardInfoRepository.findById(id).map(user -> {
                    cardInfoCacheService.save(cardInfoMapper.toCacheDto(user));
                    return cardInfoMapper.toDto(user);
                }))
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", id);
                    return new CardInfoNotFoundException("Card with id " + id + " not found");
                });
    }

    @Override
    public List<CardInfoResponse> getCardsByIds(List<Long> ids) {
        log.info("Fetching cards by ids: {}", ids);

        if (ids == null || ids.isEmpty()) {
            log.warn("Empty or null list of ids provided to getCardsByIds");
            throw new IllegalArgumentException("List of IDs must not be empty");
        }

        List<CardInfo> cards = cardInfoRepository.findAllById(ids);
        if (cards.isEmpty()) {
            log.error("No cards found for given ids: {}", ids);
            throw new CardInfoNotFoundException("No cards found for given ids: " + ids);
        }

        cards.forEach(cardInfo -> cardInfoCacheService.save(cardInfoMapper.toCacheDto(cardInfo)));

        return cards.stream().map(cardInfoMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CardInfoResponse updateCard(Long id, CardInfoRequest request) {
        log.info("Updating card with id: {}", id);

        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card not found for update with id: {}", id);
                    return new CardInfoNotFoundException("Card not found: " + id);
                });

        if (!card.getNumber().equals(request.number())
                && cardInfoRepository.findByNumber(request.number()).isPresent()) {
            log.warn("Duplicate card number detected during update: {}", request.number());
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to update card with expired date: {}", request.expirationDate());
            throw new ExpiredCardException("Cannot register an expired card");
        }

        cardInfoMapper.update(card, request);

        if (!card.getUser().getId().equals(request.userId())) {
            card.setUser(userService.getEntityById(request.userId()));
        }

        CardInfo saved = cardInfoRepository.save(card);

        cardInfoCacheService.save(cardInfoMapper.toCacheDto(saved));

        CardInfoResponse response = cardInfoMapper.toDto(saved);
        log.info("Card updated with id: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        log.info("Deleting card with id: {}", id);

        if (!cardInfoRepository.existsById(id)) {
            log.error("Card not found for deletion with id: {}", id);
            throw new CardInfoNotFoundException("Card not found for id: " + id);
        }

        cardInfoRepository.deleteById(id);
        cardInfoCacheService.deleteById(id);

        log.info("Card deleted with id: {}", id);
    }

    @Override
    public List<CardInfoResponse> getCardsByUserId(Long userId) {
        return cardInfoRepository.findByUserId(userId)
                .stream()
                .map(cardInfoMapper::toDto)
                .collect(Collectors.toList());
    }
}

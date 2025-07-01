package org.ebndrnk.userservice.service.card;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.dto.card.CardInfoNotFoundException;
import org.ebndrnk.userservice.exception.dto.card.DuplicateCardNumberException;
import org.ebndrnk.userservice.exception.dto.card.ExpiredCardException;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.mapper.CardInfoMapper;
import org.ebndrnk.userservice.repository.CardInfoRepository;
import org.ebndrnk.userservice.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserService userService;
    private final CardInfoMapper cardInfoMapper;

    @Override
    public CardInfoResponse createCard(CardInfoRequest request) {
        log.info("Creating card with number: {}", request.number());

        if (cardInfoRepository.findByNumber(request.number()).isPresent()) {
            log.warn("Duplicate card number attempted: {}", request.number());
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to create card with expired date: {}", request.expirationDate());
            throw new ExpiredCardException();
        }

        CardInfo cardInfo = cardInfoMapper.toEntity(request);
        cardInfo.setUser(userService.getEntityById(request.userId()));

        CardInfoResponse response = cardInfoMapper.toDto(cardInfoRepository.save(cardInfo));
        log.info("Card created with id: {}", response.id());
        return response;
    }

    @Override
    public CardInfoResponse getCardById(Long id) {
        log.info("Fetching card by id: {}", id);
        return cardInfoRepository.findById(id)
                .map(cardInfoMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Card not found with id: {}", id);
                    return new CardInfoNotFoundException("Card not found with id: " + id);
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

        if (!card.getNumber().equals(request.number()) &&
                cardInfoRepository.findByNumber(request.number()).isPresent()) {
            log.warn("Duplicate card number detected during update: {}", request.number());
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to update card with expired date: {}", request.expirationDate());
            throw new ExpiredCardException();
        }

        cardInfoMapper.update(card, request);

        if (!card.getUser().getId().equals(request.userId())) {
            card.setUser(userService.getEntityById(request.userId()));
        }

        CardInfoResponse response = cardInfoMapper.toDto(cardInfoRepository.save(card));
        log.info("Card updated with id: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        log.info("Deleting card with id: {}", id);

        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Card not found for deletion with id: {}", id);
                    return new CardInfoNotFoundException("Card not found: " + id);
                });

        cardInfoRepository.delete(card);
        log.info("Card deleted with id: {}", id);
    }
}

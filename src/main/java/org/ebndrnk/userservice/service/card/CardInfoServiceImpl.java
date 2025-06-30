package org.ebndrnk.userservice.service.card;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserService userService;
    private final CardInfoMapper cardInfoMapper;

    @Override
    public CardInfoResponse createCard(CardInfoRequest request) {
        if (cardInfoRepository.findByNumber(request.number()).isPresent()) {
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException();
        }

        CardInfo cardInfo = cardInfoMapper.toEntity(request);
        cardInfo.setUser(userService.getEntityById(request.userId()));

        return cardInfoMapper.toDto(cardInfoRepository.save(cardInfo));
    }

    @Override
    public CardInfoResponse getCardById(Long id) {
        return cardInfoRepository.findById(id)
                .map(cardInfoMapper::toDto)
                .orElseThrow(() -> new CardInfoNotFoundException("Card not found with id: " + id));
    }

    @Override
    public List<CardInfoResponse> getCardsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List of IDs must not be empty");
        }

        List<CardInfo> cards = cardInfoRepository.findAllById(ids);
        if (cards.isEmpty()) {
            throw new CardInfoNotFoundException("No cards found for given ids: " + ids);
        }

        return cards.stream().map(cardInfoMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CardInfoResponse updateCard(Long id, CardInfoRequest request) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardInfoNotFoundException("Card not found: " + id));

        if (!card.getNumber().equals(request.number()) &&
                cardInfoRepository.findByNumber(request.number()).isPresent()) {
            throw new DuplicateCardNumberException(request.number());
        }

        if (request.expirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException();
        }

        cardInfoMapper.update(card, request);

        if (!card.getUser().getId().equals(request.userId())) {
            card.setUser(userService.getEntityById(request.userId()));
        }

        return cardInfoMapper.toDto(cardInfoRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardInfoNotFoundException("Card not found: " + id));

        cardInfoRepository.delete(card);
    }
}

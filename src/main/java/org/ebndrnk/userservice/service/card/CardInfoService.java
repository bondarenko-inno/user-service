package org.ebndrnk.userservice.service.card;

import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;

import java.util.List;

public interface CardInfoService {
    CardInfoResponse createCard(CardInfoRequest request);

    CardInfoResponse getCardById(Long id);

    List<CardInfoResponse> getCardsByIds(List<Long> ids);

    CardInfoResponse updateCard(Long id, CardInfoRequest request);

    void deleteCard(Long id);
}

package org.ebndrnk.userservice.service.card;

import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;

import java.util.List;

/**
 * Service interface for managing card information.
 */
public interface CardInfoService {

    /**
     * Creates a new card based on the provided request.
     *
     * @param request DTO containing card details to create.
     * @return the created card information as a {@link CardInfoResponse}.
     */
    CardInfoResponse createCard(CardInfoRequest request);

    /**
     * Retrieves a card by its unique identifier.
     *
     * @param id the unique identifier of the card.
     * @return the card information as a {@link CardInfoResponse}.
     */
    CardInfoResponse getCardById(Long id);

    /**
     * Retrieves a list of cards by their IDs.
     *
     * @param ids list of card IDs to fetch.
     * @return list of {@link CardInfoResponse}.
     */
    List<CardInfoResponse> getCardsByIds(List<Long> ids);

    /**
     * Updates an existing card's data.
     *
     * @param id      the ID of the card to update.
     * @param request DTO containing the updated card data.
     * @return the updated card information as a {@link CardInfoResponse}.
     */
    CardInfoResponse updateCard(Long id, CardInfoRequest request);

    /**
     * Deletes a card by its unique identifier.
     *
     * @param id the ID of the card to delete.
     */
    void deleteCard(Long id);

    List<CardInfoResponse> getCardsByUserId(Long userId);
}

package org.ebndrnk.userservice.controller.card;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.service.card.CardInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CardInfo Controller
 *
 * <p>
 * This controller handles all operations related to user cards, such as creating, updating,
 * deleting, and retrieving card information.
 * </p>
 *
 * <p>
 * All logic is delegated to {@link CardInfoService}.
 * </p>
 */
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Card Information", description = "Endpoints for managing user cards")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    /**
     * Create a new card.
     *
     * <p>
     * This endpoint creates a new card and returns the created card information.
     * </p>
     *
     * @param request the card information request containing card details.
     * @return a response containing the created card information.
     */
    @PostMapping
    @Operation(summary = "Create a new card",
            description = "Creates a new card and returns the card information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<CardInfoResponse> createCard(@Valid @RequestBody CardInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoService.createCard(request));
    }

    /**
     * Retrieve card information by ID.
     *
     * <p>
     * This endpoint retrieves the information of a card specified by its ID.
     * </p>
     *
     * @param id the ID of the card to retrieve.
     * @return a response containing the card information.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID",
            description = "Retrieves card information for the specified card ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
    })
    public ResponseEntity<CardInfoResponse> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    /**
     * Retrieve multiple cards by IDs.
     *
     * <p>
     * This endpoint retrieves the information of multiple cards specified by their IDs.
     * </p>
     *
     * @param ids the list of card IDs to retrieve.
     * @return a response containing the list of card information.
     */
    @GetMapping
    @Operation(summary = "Get cards by IDs",
            description = "Retrieves information for multiple cards based on their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "One or more cards not found"),
    })
    public ResponseEntity<List<CardInfoResponse>> getCardsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(cardInfoService.getCardsByIds(ids));
    }


    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<CardInfoResponse>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardInfoService.getCardsByUserId(userId));
    }

    /**
     * Update card information by ID.
     *
     * <p>
     * This endpoint updates the information of a card specified by its ID.
     * </p>
     *
     * @param id      the ID of the card to update.
     * @param request the updated card information.
     * @return a response containing the updated card information.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update card by ID",
            description = "Updates the information of a specified card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<CardInfoResponse> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardInfoRequest request
    ) {
        return ResponseEntity.ok(cardInfoService.updateCard(id, request));
    }

    /**
     * Delete card by ID.
     *
     * <p>
     * This endpoint deletes a card specified by its ID.
     * </p>
     *
     * @param id the ID of the card to delete.
     * @return a response indicating the deletion status.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card by ID",
            description = "Deletes the card specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
    })
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
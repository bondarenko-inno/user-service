package org.ebndrnk.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebndrnk.userservice.controller.card.CardInfoController;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.service.card.CardInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(CardInfoController.class)
class CardInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardInfoService cardInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardInfoRequest validRequest;
    private CardInfoResponse validResponse;

    @BeforeEach
    void setup() {
        validRequest = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().plusYears(1),
                "John Doe",
                1L
        );

        validResponse = new CardInfoResponse(
                10L,
                "1234567812345678",
                "John Doe",
                LocalDateTime.now().plusYears(1),
                1L
        );
    }

    /**
     * Test creating a new card.
     * Given a valid CardInfoRequest,
     * When POST /api/cards is called,
     * Then response status is 201 Created,
     * And response body contains the created card details.
     */
    @Test
    void createCard_ShouldReturnCreatedCard() throws Exception {
        Mockito.when(cardInfoService.createCard(any(CardInfoRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validResponse.id()))
                .andExpect(jsonPath("$.number").value(validResponse.number()))
                .andExpect(jsonPath("$.holder").value(validResponse.holder()));
    }

    /**
     * Test fetching a card by ID.
     * Given an existing card ID,
     * When GET /api/cards/{id} is called,
     * Then response status is 200 OK,
     * And response body contains card details.
     */
    @Test
    void getCardById_ShouldReturnCard() throws Exception {
        Mockito.when(cardInfoService.getCardById(10L)).thenReturn(validResponse);

        mockMvc.perform(get("/api/cards/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.number").value(validResponse.number()))
                .andExpect(jsonPath("$.holder").value(validResponse.holder()));
    }

    /**
     * Test fetching multiple cards by IDs.
     * Given a list of card IDs,
     * When GET /api/cards?ids=... is called,
     * Then response status is 200 OK,
     * And response body contains list of cards.
     */
    @Test
    void getCardsByIds_ShouldReturnCardsList() throws Exception {
        List<CardInfoResponse> cards = List.of(validResponse);
        Mockito.when(cardInfoService.getCardsByIds(List.of(10L, 11L))).thenReturn(cards);

        mockMvc.perform(get("/api/cards")
                        .param("ids", "10", "11"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10));
    }

    /**
     * Test updating a card by ID.
     * Given an existing card ID and valid CardInfoRequest,
     * When PUT /api/cards/{id} is called,
     * Then response status is 200 OK,
     * And response body contains updated card info.
     */
    @Test
    void updateCard_ShouldReturnUpdatedCard() throws Exception {
        Mockito.when(cardInfoService.updateCard(eq(10L), any(CardInfoRequest.class))).thenReturn(validResponse);

        mockMvc.perform(put("/api/cards/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    /**
     * Test deleting a card by ID.
     * Given an existing card ID,
     * When DELETE /api/cards/{id} is called,
     * Then response status is 204 No Content.
     */
    @Test
    void deleteCard_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(cardInfoService).deleteCard(10L);

        mockMvc.perform(delete("/api/cards/10"))
                .andExpect(status().isNoContent());
    }
}

package org.ebndrnk.userservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebndrnk.userservice.config.TestContainersConfig;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for CardInfoController.
 * Tests card creation, retrieval, update, deletion, and error scenarios.
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class CardInfoControllerIntegrationTest extends TestContainersConfig {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DataSource dataSource;

    private UserResponse user;
    private CardInfoRequest cardRequest;

    /**
     * Sets up the test environment by truncating relevant tables,
     * flushing Redis, and creating a test user and card request.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        var connection = dataSource.getConnection();
        try (var stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE card_info RESTART IDENTITY CASCADE;");
            stmt.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE;");
        }
        connection.close();

        redisTemplate.getConnectionFactory().getConnection().flushDb();

        user = userService.createUser(new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 1, 1, 0, 0)
        ));

        cardRequest = new CardInfoRequest(
                "1234567890123456",
                LocalDateTime.now().plusYears(1),
                "holder",
                user.id()
        );
    }

    /**
     * Tests successful creation of a card.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(1)
    void createCard_success() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.number").value("1234567890123456"));
    }

    /**
     * Tests error when creating a card with duplicate number.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(2)
    void createCard_duplicateNumber() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Card with number 1234567890123456 already exists"));
    }

    /**
     * Tests error when creating a card with an expired expiration date.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(3)
    void createCard_expiredDate() throws Exception {
        CardInfoRequest expired = new CardInfoRequest(
                "9999888877776666",
                LocalDateTime.now().minusDays(1),
                "holder",
                user.id()
        );

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expired)))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.message").value("Cannot register an expired card"));
    }

    /**
     * Tests successful retrieval of a card by ID.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(4)
    void getCardById_success() throws Exception {
        var response = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andReturn();

        Long id = objectMapper.readTree(response.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/cards/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.number").value("1234567890123456"));
    }

    /**
     * Tests error when retrieving a card by a non-existent ID.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(5)
    void getCardById_notFound() throws Exception {
        mockMvc.perform(get("/api/cards/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Card with id 999 not found"));
    }

    /**
     * Tests successful retrieval of multiple cards by their IDs.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(6)
    void getCardsByIds_success() throws Exception {
        var created1 = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andReturn();

        Long id1 = objectMapper.readTree(created1.getResponse().getContentAsString()).get("id").asLong();

        CardInfoRequest second = new CardInfoRequest(
                "5555666677778888",
                LocalDateTime.now().plusYears(2),
                "holder",
                user.id()
        );

        var created2 = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andReturn();

        Long id2 = objectMapper.readTree(created2.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/cards")
                        .param("ids", String.valueOf(id1), String.valueOf(id2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[*].number").value(org.hamcrest.Matchers.containsInAnyOrder(
                        "1234567890123456", "5555666677778888"
                )));
    }

    /**
     * Tests error when no cards are found for given IDs.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(7)
    void getCardsByIds_notFound() throws Exception {
        mockMvc.perform(get("/api/cards")
                        .param("ids", "999", "888"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No cards found for given ids: [999, 888]"));
    }

    /**
     * Tests successful update of a card.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(8)
    void updateCard_success() throws Exception {
        var created = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        CardInfoRequest update = new CardInfoRequest(
                "4444333322221111",
                LocalDateTime.now().plusYears(2),
                "holder",
                user.id()
        );

        mockMvc.perform(put("/api/cards/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("4444333322221111"));
    }

    /**
     * Tests error when updating a non-existent card.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(9)
    void updateCard_notFound() throws Exception {
        CardInfoRequest update = new CardInfoRequest(
                "4444333322221111",
                LocalDateTime.now().plusYears(2),
                "holder",
                user.id()
        );

        mockMvc.perform(put("/api/cards/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Card not found: 999"));
    }

    /**
     * Tests successful deletion of a card.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(10)
    void deleteCard_success() throws Exception {
        var created = mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/cards/{id}", id))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests error when deleting a non-existent card.
     *
     * @throws Exception if the request fails
     */
    @Test
    @Order(11)
    void deleteCard_notFound() throws Exception {
        mockMvc.perform(delete("/api/cards/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Card not found for id: 999"));
    }
}

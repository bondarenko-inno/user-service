package org.ebndrnk.userservice.integration.service;

import org.ebndrnk.userservice.config.TestContainersConfig;
import org.ebndrnk.userservice.exception.card.CardInfoNotFoundException;
import org.ebndrnk.userservice.exception.card.DuplicateCardNumberException;
import org.ebndrnk.userservice.exception.card.ExpiredCardException;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.service.card.CardInfoCacheService;
import org.ebndrnk.userservice.service.card.CardInfoService;
import org.ebndrnk.userservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for CardInfoService.
 */
@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class CardInfoServiceIntegrationTest extends TestContainersConfig {

    @Autowired
    CardInfoService cardInfoService;

    @Autowired
    CardInfoRepository cardInfoRepository;

    @Autowired
    CardInfoCacheService cardInfoCacheService;

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DataSource dataSource;

    private UserResponse user;

    private CardInfoRequest cardRequest;

    /**
     * Sets up test data before each test by clearing DB and Redis, and creating a test user.
     *
     * @throws SQLException if any SQL error occurs during setup
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
                "holer",
                user.id()
        );
    }

    /**
     * Tests successful creation of a card.
     */
    @Test
    @Order(1)
    void createCard_success() {
        CardInfoResponse created = cardInfoService.createCard(cardRequest);

        assertThat(created.id()).isNotNull();
        assertThat(created.number()).isEqualTo("1234567890123456");

        CardInfo entity = cardInfoRepository.findById(created.id()).orElseThrow();
        assertThat(entity.getNumber()).isEqualTo("1234567890123456");
        assertThat(entity.getUser().getId()).isEqualTo(user.id());

        Optional<CardInfoCacheDto> cached = cardInfoCacheService.findById(created.id());
        assertThat(cached).isPresent();
        assertThat(cached.get().number()).isEqualTo("1234567890123456");
    }

    /**
     * Tests that creating a card with a duplicate number throws DuplicateCardNumberException.
     */
    @Test
    @Order(2)
    void createCard_duplicateNumber() {
        cardInfoService.createCard(cardRequest);

        assertThatThrownBy(() -> cardInfoService.createCard(cardRequest))
                .isInstanceOf(DuplicateCardNumberException.class);
    }

    /**
     * Tests that creating a card with an expired date throws ExpiredCardException.
     */
    @Test
    @Order(3)
    void createCard_expiredDate() {
        CardInfoRequest expired = new CardInfoRequest(
                "9876543210987654",
                LocalDateTime.now().minusDays(1),
                "holder",
                user.id()
        );

        assertThatThrownBy(() -> cardInfoService.createCard(expired))
                .isInstanceOf(ExpiredCardException.class);
    }

    /**
     * Tests successful retrieval of a card by ID.
     */
    @Test
    @Order(4)
    void getCardById_success() {
        CardInfoResponse created = cardInfoService.createCard(cardRequest);

        CardInfoResponse found = cardInfoService.getCardById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.number()).isEqualTo("1234567890123456");

        Optional<CardInfoCacheDto> cached = cardInfoCacheService.findById(created.id());
        assertThat(cached).isPresent();
    }

    /**
     * Tests that retrieving a non-existing card by ID throws CardInfoNotFoundException.
     */
    @Test
    @Order(5)
    void getCardById_notFound() {
        assertThatThrownBy(() -> cardInfoService.getCardById(999L))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    /**
     * Tests successful retrieval of multiple cards by their IDs.
     */
    @Test
    @Order(6)
    void getCardsByIds_success() {
        CardInfoResponse c1 = cardInfoService.createCard(cardRequest);

        CardInfoRequest second = new CardInfoRequest(
                "5555666677778888",
                LocalDateTime.now().plusYears(2),
                "holder",
                user.id()
        );

        CardInfoResponse c2 = cardInfoService.createCard(second);

        List<CardInfoResponse> cards = cardInfoService.getCardsByIds(List.of(c1.id(), c2.id()));

        assertThat(cards).hasSize(2);
        assertThat(cards).extracting(CardInfoResponse::number)
                .containsExactlyInAnyOrder("1234567890123456", "5555666677778888");
    }

    /**
     * Tests that retrieving cards by invalid IDs throws CardInfoNotFoundException.
     */
    @Test
    @Order(7)
    void getCardsByIds_notFound() {
        assertThatThrownBy(() -> cardInfoService.getCardsByIds(List.of(999L, 888L)))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    /**
     * Tests successful update of a card.
     */
    @Test
    @Order(8)
    void updateCard_success() {
        CardInfoResponse created = cardInfoService.createCard(cardRequest);

        CardInfoRequest updatedReq = new CardInfoRequest(
                "4444333322221111",
                LocalDateTime.now().plusYears(3),
                "holder",
                user.id()
        );

        CardInfoResponse updated = cardInfoService.updateCard(created.id(), updatedReq);

        assertThat(updated.number()).isEqualTo("4444333322221111");

        Optional<CardInfoCacheDto> cached = cardInfoCacheService.findById(created.id());
        assertThat(cached).isPresent();
        assertThat(cached.get().number()).isEqualTo("4444333322221111");
    }

    /**
     * Tests that updating a non-existing card throws CardInfoNotFoundException.
     */
    @Test
    @Order(9)
    void updateCard_notFound() {
        CardInfoRequest updatedReq = new CardInfoRequest(
                "4444333322221111",
                LocalDateTime.now().plusYears(3),
                "holder",
                user.id()
        );

        assertThatThrownBy(() -> cardInfoService.updateCard(999L, updatedReq))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    /**
     * Tests that updating a card with a duplicate number throws DuplicateCardNumberException.
     */
    @Test
    @Order(10)
    void updateCard_duplicateNumber() {
        CardInfoResponse card1 = cardInfoService.createCard(cardRequest);

        CardInfoRequest secondReq = new CardInfoRequest(
                "9999888877776666",
                LocalDateTime.now().plusYears(1),
                "holder",
                user.id()
        );

        CardInfoResponse card2 = cardInfoService.createCard(secondReq);

        CardInfoRequest duplicateNumberReq = new CardInfoRequest(
                "1234567890123456", // number from card1
                LocalDateTime.now().plusYears(1),
                "holder",
                user.id()
        );

        assertThatThrownBy(() -> cardInfoService.updateCard(card2.id(), duplicateNumberReq))
                .isInstanceOf(DuplicateCardNumberException.class);
    }

    /**
     * Tests that updating a card with an expired date throws ExpiredCardException.
     */
    @Test
    @Order(11)
    void updateCard_expiredDate() {
        CardInfoResponse created = cardInfoService.createCard(cardRequest);

        CardInfoRequest expired = new CardInfoRequest(
                "5555666677778888",
                LocalDateTime.now().minusDays(1),
                "holder",
                user.id()
        );

        assertThatThrownBy(() -> cardInfoService.updateCard(created.id(), expired))
                .isInstanceOf(ExpiredCardException.class);
    }

    /**
     * Tests successful deletion of a card.
     */
    @Test
    @Order(12)
    void deleteCard_success() {
        CardInfoResponse created = cardInfoService.createCard(cardRequest);

        cardInfoService.deleteCard(created.id());

        assertThat(cardInfoRepository.existsById(created.id())).isFalse();

        assertThat(cardInfoCacheService.findById(created.id())).isNotPresent();
    }

    /**
     * Tests that deleting a non-existing card throws CardInfoNotFoundException.
     */
    @Test
    @Order(13)
    void deleteCard_notFound() {
        assertThatThrownBy(() -> cardInfoService.deleteCard(999L))
                .isInstanceOf(CardInfoNotFoundException.class);
    }
}

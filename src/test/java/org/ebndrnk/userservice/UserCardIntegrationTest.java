package org.ebndrnk.userservice;

import jakarta.transaction.Transactional;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    /**
     * Test cascade delete behavior.
     * Given a user with two cards saved in the database,
     * When the user is deleted,
     * Then the user and their cards are removed from the database.
     */
    @Test
    void testCascadeDelete() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setSurname("User");
        user.setEmail("test@example.com");
        user.setBirthDate(LocalDateTime.of(1990, 1, 1, 0, 0));

        CardInfo card1 = new CardInfo();
        card1.setNumber("1111222233334444");
        card1.setHolder("Test User");
        card1.setExpirationDate(LocalDateTime.now().plusYears(1));
        card1.setUser(user);

        CardInfo card2 = new CardInfo();
        card2.setNumber("5555666677778888");
        card2.setHolder("Test User");
        card2.setExpirationDate(LocalDateTime.now().plusYears(1));
        card2.setUser(user);

        user.getCards().add(card1);
        user.getCards().add(card2);

        userRepository.save(user);

        Long userId = user.getId();
        Long card1Id = card1.getId();
        Long card2Id = card2.getId();

        assertTrue(userRepository.existsById(userId));
        assertTrue(cardInfoRepository.existsById(card1Id));
        assertTrue(cardInfoRepository.existsById(card2Id));

        userRepository.deleteById(userId);

        assertFalse(userRepository.existsById(userId));
        assertFalse(cardInfoRepository.existsById(card1Id));
        assertFalse(cardInfoRepository.existsById(card2Id));
    }
}

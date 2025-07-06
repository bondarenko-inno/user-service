package org.ebndrnk.userservice.unit.service.card;

import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.repository.card.CardInfoRedisRepository;
import org.ebndrnk.userservice.service.card.CardInfoCacheService;
import org.ebndrnk.userservice.service.card.CardInfoCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CardInfoCacheServiceImpl}.
 */
class CardInfoCacheServiceImplTest {

    private CardInfoRedisRepository cardRedisRepository;
    private CardInfoCacheService cardCacheService;

    /**
     * Initializes mocks and service instance before each test.
     */
    @BeforeEach
    void setUp() {
        cardRedisRepository = mock(CardInfoRedisRepository.class);
        cardCacheService = new CardInfoCacheServiceImpl(cardRedisRepository);
    }

    /**
     * Tests that findById returns CardInfoCacheDto when it exists in the repository.
     */
    @Test
    void findById_shouldReturnCardInfoCacheDto_whenExists() {
        Long cardId = 10L;
        CardInfoCacheDto card = new CardInfoCacheDto(cardId, "1234567812345678", "John Doe",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59), 1L);

        when(cardRedisRepository.findById(cardId)).thenReturn(Optional.of(card));

        Optional<CardInfoCacheDto> result = cardCacheService.findById(cardId);

        assertTrue(result.isPresent());
        assertEquals(cardId, result.get().id());
        assertEquals("1234567812345678", result.get().number());
        assertEquals("John Doe", result.get().holder());
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59, 59), result.get().expirationDate());
        assertEquals(1L, result.get().userId());
        verify(cardRedisRepository, times(1)).findById(cardId);
    }

    /**
     * Tests that findById returns empty Optional when an exception is thrown by the repository.
     */
    @Test
    void findById_shouldReturnEmptyOptional_whenExceptionThrown() {
        Long cardId = 10L;
        when(cardRedisRepository.findById(cardId)).thenThrow(new RuntimeException("Redis error"));

        Optional<CardInfoCacheDto> result = cardCacheService.findById(cardId);

        assertTrue(result.isEmpty());
        verify(cardRedisRepository, times(1)).findById(cardId);
    }

    /**
     * Tests that save calls the repository's save method.
     */
    @Test
    void save_shouldCallRepositorySave() {
        CardInfoCacheDto card = new CardInfoCacheDto(20L, "8765432187654321", "Jane Smith",
                LocalDateTime.now().plusYears(1), 2L);

        cardCacheService.save(card);

        verify(cardRedisRepository, times(1)).save(card);
    }

    /**
     * Tests that save handles exceptions thrown by the repository gracefully.
     */
    @Test
    void save_shouldLogError_whenExceptionThrown() {
        CardInfoCacheDto card = new CardInfoCacheDto(20L, "8765432187654321", "Jane Smith",
                LocalDateTime.now().plusYears(1), 2L);

        doThrow(new RuntimeException("Redis error"))
                .when(cardRedisRepository).save(card);

        cardCacheService.save(card);

        verify(cardRedisRepository, times(1)).save(card);
    }

    /**
     * Tests that deleteById calls the repository's deleteById method.
     */
    @Test
    void deleteById_shouldCallRepositoryDelete() {
        Long cardId = 30L;

        cardCacheService.deleteById(cardId);

        verify(cardRedisRepository, times(1)).deleteById(cardId);
    }

    /**
     * Tests that deleteById handles exceptions thrown by the repository gracefully.
     */
    @Test
    void deleteById_shouldLogError_whenExceptionThrown() {
        Long cardId = 30L;

        doThrow(new RuntimeException("Redis error"))
                .when(cardRedisRepository).deleteById(cardId);

        cardCacheService.deleteById(cardId);

        verify(cardRedisRepository, times(1)).deleteById(cardId);
    }
}

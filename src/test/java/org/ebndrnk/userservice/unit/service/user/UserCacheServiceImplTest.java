package org.ebndrnk.userservice.unit.service.user;

import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.repository.user.UserRedisRepository;
import org.ebndrnk.userservice.service.user.UserCacheService;
import org.ebndrnk.userservice.service.user.UserCacheServiceImpl;
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

class UserCacheServiceImplTest {

    private UserRedisRepository userRedisRepository;
    private UserCacheService userCacheService;

    @BeforeEach
    void setUp() {
        userRedisRepository = mock(UserRedisRepository.class);
        userCacheService = new UserCacheServiceImpl(userRedisRepository);
    }

    @Test
    void findById_shouldReturnUserCacheDto_whenExists() {
        Long userId = 1L;
        UserCacheDto user = new UserCacheDto(userId, "John", "Doe", "john.doe@example.com", LocalDateTime.of(1990, 5, 15, 0, 0));

        when(userRedisRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<UserCacheDto> result = userCacheService.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().id());
        assertEquals("john.doe@example.com", result.get().email());
        assertEquals("John", result.get().name());
        assertEquals("Doe", result.get().surname());
        assertEquals(LocalDateTime.of(1990, 5, 15, 0, 0), result.get().birthDate());
        verify(userRedisRepository, times(1)).findById(userId);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenExceptionThrown() {
        Long userId = 1L;
        when(userRedisRepository.findById(userId)).thenThrow(new RuntimeException("Redis error"));

        Optional<UserCacheDto> result = userCacheService.findById(userId);

        assertTrue(result.isEmpty());
        verify(userRedisRepository, times(1)).findById(userId);
    }

    @Test
    void save_shouldCallRepositorySave() {
        UserCacheDto user = new UserCacheDto(2L, "Jane", "Smith", "jane.smith@example.com", LocalDateTime.now());

        userCacheService.save(user);

        verify(userRedisRepository, times(1)).save(user);
    }

    @Test
    void save_shouldLogError_whenExceptionThrown() {
        UserCacheDto user = new UserCacheDto(2L, "Jane", "Smith", "jane.smith@example.com", LocalDateTime.now());

        doThrow(new RuntimeException("Redis error"))
                .when(userRedisRepository).save(user);

        userCacheService.save(user);

        verify(userRedisRepository, times(1)).save(user);
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        Long userId = 3L;

        userCacheService.deleteById(userId);

        verify(userRedisRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteById_shouldLogError_whenExceptionThrown() {
        Long userId = 3L;

        doThrow(new RuntimeException("Redis error"))
                .when(userRedisRepository).deleteById(userId);

        userCacheService.deleteById(userId);

        verify(userRedisRepository, times(1)).deleteById(userId);
    }
}

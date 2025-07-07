package org.ebndrnk.userservice.unit.service.user;

import org.ebndrnk.userservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.userservice.exception.dto.user.UserNotFoundException;
import org.ebndrnk.userservice.mapper.UserMapper;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.repository.user.UserRepository;
import org.ebndrnk.userservice.service.card.CardInfoCacheService;
import org.ebndrnk.userservice.service.user.UserCacheService;
import org.ebndrnk.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 * <p>
 * These tests verify the behavior of {@link UserServiceImpl} in various
 * scenarios, ensuring correct handling of user creation, retrieval,
 * updating, and deletion operations, as well as proper interaction
 * with caching and repositories.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private UserCacheService userCacheService;
    private UserMapper userMapper;
    private CardInfoRepository cardInfoRepository;
    private UserServiceImpl userService;

    /**
     * Sets up mocks and creates the service instance before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userCacheService = mock(UserCacheService.class);
        userMapper = mock(UserMapper.class);
        cardInfoRepository = mock(CardInfoRepository.class);

        userService = new UserServiceImpl(
                userRepository,
                userCacheService,
                userMapper,
                mock(CardInfoCacheService.class),
                cardInfoRepository
        );
    }

    /**
     * Tests that {@link UserServiceImpl#createUser(UserRequest)} successfully creates a user
     * when no duplicate email exists, and saves the user in the cache.
     */
    @Test
    void createUser_success() {
        var request = new UserRequest("John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var user = new User();
        var savedUser = new User();
        savedUser.setId(1L);
        var response = new UserResponse(1L, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var cacheDto = new UserCacheDto(1L, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toCacheDto(savedUser)).thenReturn(cacheDto);
        when(userMapper.toDto(savedUser)).thenReturn(response);

        var result = userService.createUser(request);

        assertThat(result).isEqualTo(response);
        verify(userCacheService).save(cacheDto);
    }

    /**
     * Tests that {@link UserServiceImpl#createUser(UserRequest)} throws
     * {@link DuplicateEmailException} if a user with the same email already exists.
     */
    @Test
    void createUser_duplicateEmail_throwsException() {
        var request = new UserRequest("John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateEmailException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#getUserById(Long)} fetches a user from the database
     * if not found in the cache, and saves it to the cache afterward.
     */
    @Test
    void getUserById_fromDb_success() {
        var id = 1L;
        var user = new User();
        var dto = new UserResponse(id, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var cacheDto = new UserCacheDto(id, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userCacheService.findById(id)).thenReturn(Optional.empty());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toCacheDto(user)).thenReturn(cacheDto);
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = userService.getUserById(id);

        assertThat(result).isEqualTo(dto);
        verify(userCacheService).save(cacheDto);
    }

    /**
     * Tests that {@link UserServiceImpl#getUserById(Long)} throws
     * {@link UserNotFoundException} if the user does not exist in either cache or database.
     */
    @Test
    void getUserById_notFound() {
        var id = 1L;

        when(userCacheService.findById(id)).thenReturn(Optional.empty());
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#getUsersById(List)} returns a list of users
     * for given IDs and converts them to DTOs.
     */
    @Test
    void getUsersById_success() {
        var ids = List.of(1L, 2L);
        var user1 = new User();
        user1.setId(1L);
        var user2 = new User();
        user2.setId(2L);
        var response1 = new UserResponse(1L, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var response2 = new UserResponse(2L, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findAllById(ids)).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(response1);
        when(userMapper.toDto(user2)).thenReturn(response2);
        when(userMapper.toCacheDto(any())).thenReturn(mock(UserCacheDto.class));

        var result = userService.getUsersById(ids);

        assertThat(result).containsExactly(response1, response2);
    }

    /**
     * Tests that {@link UserServiceImpl#getUsersById(List)} throws
     * {@link IllegalArgumentException} when an empty list of IDs is provided.
     */
    @Test
    void getUsersById_emptyList_throws() {
        assertThatThrownBy(() -> userService.getUsersById(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#getUsersById(List)} throws
     * {@link UserNotFoundException} when no users are found for the provided IDs.
     */
    @Test
    void getUsersById_noUsersFound_throws() {
        var ids = List.of(99L, 100L);

        when(userRepository.findAllById(ids)).thenReturn(List.of());

        assertThatThrownBy(() -> userService.getUsersById(ids))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#getUserByEmail(String)} returns
     * a user DTO if the user exists.
     */
    @Test
    void getUserByEmail_found() {
        var email = "john@example.com";
        var user = new User();
        var dto = new UserResponse(1L, "John",
                "Doe",
                email,
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = userService.getUserByEmail(email);

        assertThat(result).isEqualTo(dto);
    }

    /**
     * Tests that {@link UserServiceImpl#getUserByEmail(String)} throws
     * {@link UserNotFoundException} if no user is found for the given email.
     */
    @Test
    void getUserByEmail_notFound_returnsNull() {
        var email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#updateUser(Long, UserRequest)} successfully updates
     * user data and saves the updated user in the cache.
     */
    @Test
    void updateUser_success() {
        var id = 1L;
        var request = new UserRequest("John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        var existing = new User();
        existing.setEmail("old.email@example.com");

        var updated = new User();
        var response = new UserResponse(id, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var cacheDto = new UserCacheDto(id, "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userMapper.toEntity(request)).thenReturn(updated);
        when(userRepository.save(existing)).thenReturn(updated);
        when(userMapper.toDto(updated)).thenReturn(response);
        when(userMapper.toCacheDto(updated)).thenReturn(cacheDto);

        var result = userService.updateUser(id, request);

        assertThat(result).isEqualTo(response);
        verify(userCacheService).save(cacheDto);
    }

    /**
     * Tests that {@link UserServiceImpl#updateUser(Long, UserRequest)} throws
     * {@link UserNotFoundException} if the user does not exist.
     */
    @Test
    void updateUser_notFound_throws() {
        var id = 1L;
        var request = new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(id, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#deleteUser(Long)} deletes a user and
     * also removes the user from the cache.
     */
    @Test
    void deleteUser_success() {
        var id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);
        when(cardInfoRepository.findByUserId(id)).thenReturn(List.of());

        userService.deleteUser(id);

        verify(userCacheService).deleteById(id);
        verify(userRepository).deleteById(id);
    }

    /**
     * Tests that {@link UserServiceImpl#deleteUser(Long)} throws
     * {@link UserNotFoundException} if the user does not exist.
     */
    @Test
    void deleteUser_notFound_throws() {
        var id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Tests that {@link UserServiceImpl#getEntityById(Long)} returns
     * the user entity when it exists.
     */
    @Test
    void getEntityById_found() {
        var id = 1L;
        var user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var result = userService.getEntityById(id);

        assertThat(result).isEqualTo(user);
    }

    /**
     * Tests that {@link UserServiceImpl#getEntityById(Long)} throws
     * {@link UserNotFoundException} if the user is not found.
     */
    @Test
    void getEntityById_notFound_throws() {
        var id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getEntityById(id))
                .isInstanceOf(UserNotFoundException.class);
    }
}

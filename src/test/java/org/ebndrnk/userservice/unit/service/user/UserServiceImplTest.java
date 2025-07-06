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
import org.ebndrnk.userservice.service.user.UserCacheService;
import org.ebndrnk.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCacheService userCacheService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CardInfoRepository cardInfoRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

    @Test
    void getUserById_notFound() {
        var id = 1L;

        when(userCacheService.findById(id)).thenReturn(Optional.empty());
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(UserNotFoundException.class);
    }

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

    @Test
    void getUsersById_emptyList_throws() {
        assertThatThrownBy(() -> userService.getUsersById(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUsersById_noUsersFound_throws() {
        var ids = List.of(99L, 100L);

        when(userRepository.findAllById(ids)).thenReturn(List.of());

        assertThatThrownBy(() -> userService.getUsersById(ids))
                .isInstanceOf(UserNotFoundException.class);
    }

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

    @Test
    void getUserByEmail_notFound_returnsNull() {
        var email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userService.getUserByEmail(email)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUser_success() {
        var id = 1L;
        var request = new UserRequest("John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );
        var existing = new User();
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

    @Test
    void deleteUser_success() {
        var id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);
        when(cardInfoRepository.findByUserId(id)).thenReturn(List.of());

        userService.deleteUser(id);

        verify(userCacheService).deleteById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_notFound_throws() {
        var id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getEntityById_found() {
        var id = 1L;
        var user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var result = userService.getEntityById(id);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getEntityById_notFound_throws() {
        var id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getEntityById(id))
                .isInstanceOf(UserNotFoundException.class);
    }
}

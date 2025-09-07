package org.ebndrnk.userservice.integration.service;

import org.ebndrnk.userservice.config.TestContainersConfig;
import org.ebndrnk.userservice.exception.user.DuplicateEmailException;
import org.ebndrnk.userservice.exception.user.UserNotFoundException;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.user.UserRepository;
import org.ebndrnk.userservice.service.user.UserCacheService;
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
 * Integration tests for UserService.
 */
@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class UserServiceIntegrationTest extends TestContainersConfig {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DataSource dataSource;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserCacheService userCacheServiceImpl;

    private UserRequest userRequest;

    /**
     * Setup method runs before each test.
     * Clears the users table and flushes Redis cache.
     *
     * @throws SQLException if database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        userRequest = new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        var connection = dataSource.getConnection();
        try (var stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE;");
        }
        connection.close();

        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    /**
     * Test successful creation of a user.
     */
    @Test
    @Order(1)
    void createUser_success() {
        UserResponse created = userService.createUser(userRequest);

        assertThat(created.id()).isNotNull();
        assertThat(created.email()).isEqualTo("john.doe@example.com");

        User entity = userRepository.findById(created.id()).orElseThrow();
        assertThat(entity.getName()).isEqualTo("John");

        Optional<UserCacheDto> cached = userCacheServiceImpl.findById(created.id());

        assertThat(cached).isPresent();
        assertThat(cached.get().email()).isEqualTo("john.doe@example.com");
    }

    /**
     * Test that creating a user with duplicate email throws DuplicateEmailException.
     */
    @Test
    @Order(2)
    void createUser_duplicateEmail() {
        userService.createUser(userRequest);

        assertThatThrownBy(() -> userService.createUser(userRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    /**
     * Test that getting a user by a non-existing ID throws UserNotFoundException.
     */
    @Test
    @Order(3)
    void getUserById_notFound() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test successful retrieval of users by their IDs.
     */
    @Test
    @Order(4)
    void getUsersById_success() {
        UserResponse u1 = userService.createUser(userRequest);
        UserRequest second = new UserRequest(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                LocalDateTime.of(1995, 8, 20, 0, 0)
        );
        UserResponse u2 = userService.createUser(second);

        List<UserResponse> users = userService.getUsersById(List.of(u1.id(), u2.id()));

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserResponse::email)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    /**
     * Test that getting users by invalid IDs throws UserNotFoundException.
     */
    @Test
    @Order(6)
    void getUsersById_notFound() {
        assertThatThrownBy(() -> userService.getUsersById(List.of(999L, 888L)))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test successful retrieval of a user by email.
     */
    @Test
    @Order(7)
    void getUserByEmail_success() {
        userService.createUser(userRequest);

        UserResponse found = userService.getUserByEmail("john.doe@example.com");
        assertThat(found).isNotNull();
        assertThat(found.email()).isEqualTo("john.doe@example.com");
    }

    /**
     * Test that getting a user by a non-existing email throws UserNotFoundException.
     */
    @Test
    @Order(8)
    void getUserByEmail_notFound() {
        assertThatThrownBy(() -> userService.getUserByEmail("non.existent@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test successful update of a user.
     */
    @Test
    @Order(9)
    void updateUser_success() {
        UserResponse created = userService.createUser(userRequest);

        UserRequest updatedReq = new UserRequest(
                "Updated",
                "Surname",
                "updated.email@example.com",
                LocalDateTime.of(2000, 1, 1, 0, 0)
        );

        UserResponse updated = userService.updateUser(created.id(), updatedReq);

        assertThat(updated.name()).isEqualTo("Updated");
        assertThat(updated.email()).isEqualTo("updated.email@example.com");

        Optional<UserCacheDto> cached = userCacheServiceImpl.findById(created.id());

        assertThat(cached).isPresent();
        assertThat(cached.get().email()).isEqualTo("updated.email@example.com");
    }

    /**
     * Test that updating a non-existing user throws UserNotFoundException.
     */
    @Test
    @Order(10)
    void updateUser_notFound() {
        UserRequest updatedReq = new UserRequest(
                "Updated",
                "Surname",
                "updated.email@example.com",
                LocalDateTime.of(2000, 1, 1, 0, 0)
        );

        assertThatThrownBy(() -> userService.updateUser(999L, updatedReq))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test successful deletion of a user.
     */
    @Test
    @Order(11)
    void deleteUser_success() {
        UserResponse created = userService.createUser(userRequest);

        userService.deleteUser(created.id());

        assertThat(userRepository.existsById(created.id())).isFalse();

        assertThatThrownBy(() -> userService.getUserById(created.id()))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test that deleting a non-existing user throws UserNotFoundException.
     */
    @Test
    @Order(12)
    void deleteUser_notFound() {
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Test successful retrieval of a User entity by ID.
     */
    @Test
    @Order(13)
    void getEntityById_success() {
        UserResponse created = userService.createUser(userRequest);

        User entity = userService.getEntityById(created.id());

        assertThat(entity.getEmail()).isEqualTo("john.doe@example.com");
    }

    /**
     * Test that getting a User entity by non-existing ID throws UserNotFoundException.
     */
    @Test
    @Order(14)
    void getEntityById_notFound() {
        assertThatThrownBy(() -> userService.getEntityById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }
}

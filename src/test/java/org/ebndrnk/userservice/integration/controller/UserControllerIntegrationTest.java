package org.ebndrnk.userservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebndrnk.userservice.config.TestContainersConfig;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController.
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class UserControllerIntegrationTest extends TestContainersConfig {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataSource dataSource;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private UserRequest userRequest;

    /**
     * Prepares test data and cleans up database and Redis before each test.
     *
     * @throws Exception if any error occurs during setup
     */
    @BeforeEach
    void setUp() throws Exception {
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
     * Tests successful user creation.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(1)
    void createUser_success() throws Exception {
        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserResponse user = objectMapper.readValue(json, UserResponse.class);

        assertThat(user.id()).isNotNull();
        assertThat(user.email()).isEqualTo("john.doe@example.com");
    }

    /**
     * Tests creating a user with a duplicate email results in conflict.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(2)
    void createUser_duplicateEmail() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists: john.doe@example.com"));
    }

    /**
     * Tests successful retrieval of a user by ID.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(3)
    void getUserById_success() throws Exception {
        Long userId = createUserAndGetId();

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    /**
     * Tests retrieval of a non-existing user by ID results in 404.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(4)
    void getUserById_notFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful retrieval of multiple users by IDs.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(5)
    void getUsersByIds_success() throws Exception {
        Long id1 = createUserAndGetId("john.doe@example.com");
        Long id2 = createUserAndGetId("jane.doe@example.com");

        mockMvc.perform(get("/api/users")
                        .param("ids", id1.toString(), id2.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Tests successful retrieval of a user by email.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(6)
    void getUserByEmail_success() throws Exception {
        createUserAndGetId("john.doe@example.com");

        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Tests retrieval of a non-existing user by email results in 404.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(7)
    void getUserByEmail_notFound() throws Exception {
        mockMvc.perform(get("/api/users/by-email")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful user update.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(8)
    void updateUser_success() throws Exception {
        Long id = createUserAndGetId("john.doe@example.com");

        UserRequest updateRequest = new UserRequest(
                "Updated", "User", "john.doe@example.com", LocalDateTime.now()
        );

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    /**
     * Tests update on a non-existing user results in 404.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(9)
    void updateUser_notFound() throws Exception {
        UserRequest updateRequest = new UserRequest(
                "Updated", "User", "nonexistent@example.com", LocalDateTime.now()
        );

        mockMvc.perform(put("/api/users/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful deletion of a user.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(10)
    void deleteUser_success() throws Exception {
        Long id = createUserAndGetId("john.doe@example.com");

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests deletion of a non-existing user results in 404.
     *
     * @throws Exception if request fails
     */
    @Test
    @Order(11)
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    // --- Helper methods ---

    /**
     * Creates a user with default email and returns the user ID.
     *
     * @return created user ID
     * @throws Exception if request fails
     */
    private Long createUserAndGetId() throws Exception {
        return createUserAndGetId("john.doe@example.com");
    }

    /**
     * Creates a user with the specified email and returns the user ID.
     *
     * @param email the email to use for user creation
     * @return created user ID
     * @throws Exception if request fails
     */
    private Long createUserAndGetId(String email) throws Exception {
        UserRequest request = new UserRequest(
                "John", "Doe", email, LocalDateTime.now()
        );

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse user = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        return user.id();
    }
}

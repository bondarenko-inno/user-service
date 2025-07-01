package org.ebndrnk.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    /**
     * Test user creation endpoint.
     * Given a valid UserRequest,
     * When POST /api/users is called,
     * Then response status is 201 Created,
     * And response body contains the created user's details,
     * And user is persisted in the database.
     */
    @Test
    void testCreateUser_success() throws Exception {
        UserRequest request = new UserRequest("John", "Doe", "john.doe@example.com", LocalDateTime.of(1990, 1, 1, 0, 0));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        assert(userRepository.findByEmail("john.doe@example.com").isPresent());
    }

    /**
     * Test fetching a user by non-existing ID.
     * Given an ID that does not exist,
     * When GET /users/{id} is called,
     * Then response status is 404 Not Found.
     */
    @Test
    void testGetUserById_notFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Test updating user data.
     * Given an existing user and a valid UserRequest with updated data,
     * When PUT /api/users/{id} is called,
     * Then response status is 200 OK,
     * And response body contains updated user info,
     * And database reflects the updated data.
     */
    @Test
    void testUpdateUser_success() throws Exception {
        User userNonSaved = new User();
        userNonSaved.setName("Mark");
        userNonSaved.setSurname("Twain");
        userNonSaved.setEmail("mark.twain@example.com");
        userNonSaved.setBirthDate(LocalDateTime.of(1980, 5, 5, 0, 0));

        var user = userRepository.save(userNonSaved);

        UserRequest updateRequest = new UserRequest("Jane", "Smith", "jane.smith@example.com", LocalDateTime.of(1992, 2, 2, 0, 0));

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        var updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assert(updatedUser.getSurname().equals("Smith"));
    }

    /**
     * Test deleting a user.
     * Given an existing user,
     * When DELETE /api/users/{id} is called,
     * Then response status is 204 No Content,
     * And the user is removed from the database.
     */
    @Test
    void testDeleteUser_success() throws Exception {
        User user = new User();
        user.setName("Mark");
        user.setSurname("Twain");
        user.setEmail("mark.twain@example.com");
        user.setBirthDate(LocalDateTime.of(1980, 5, 5, 0, 0));

        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assert(userRepository.findById(user.getId()).isEmpty());
    }

}

package org.ebndrnk.userservice.service.user;

import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {

    /**
     * Creates a new user based on the provided request.
     *
     * @param userRequest DTO containing user data to create.
     * @return the created user information as a {@link UserResponse}.
     */
    UserResponse createUser(UserRequest userRequest);

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the ID of the user to retrieve.
     * @return the user information as a {@link UserResponse}.
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves a list of users by their IDs.
     *
     * @param ids list of user IDs to fetch.
     * @return list of {@link UserResponse}.
     */
    List<UserResponse> getUsersById(List<Long> ids);

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address of the user.
     * @return the user information as a {@link UserResponse}.
     */
    UserResponse getUserByEmail(String email);

    /**
     * Updates an existing user.
     *
     * @param id          the ID of the user to update.
     * @param userRequest DTO containing updated user data.
     * @return the updated user information as a {@link UserResponse}.
     */
    UserResponse updateUser(Long id, UserRequest userRequest);

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the ID of the user to delete.
     */
    void deleteUser(Long id);

    /**
     * Retrieves the {@link User} entity by its ID, typically used internally for mapping or relationships.
     *
     * @param id the ID of the user entity.
     * @return the {@link User} entity.
     */
    User getEntityById(Long id);
}

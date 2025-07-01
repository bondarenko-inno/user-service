package org.ebndrnk.userservice.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User Controller
 *
 * <p>
 * This controller handles all operations related to user management, including creating,
 * updating, deleting, and retrieving user information.
 * </p>
 *
 * <p>
 * All logic is delegated to {@link UserService}.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user information")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user.
     *
     * <p>
     * This endpoint creates a new user and returns the created user information.
     * </p>
     *
     * @param request the user information request containing user details.
     * @return a response containing the created user information.
     */
    @PostMapping
    @Operation(summary = "Create a new user",
            description = "Creates a new user and returns the user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    /**
     * Retrieve user information by ID.
     *
     * <p>
     * This endpoint retrieves the information of a user specified by their ID.
     * </p>
     *
     * @param id the ID of the user to retrieve.
     * @return a response containing the user information.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "Retrieves user information for the specified user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieve multiple users by IDs.
     *
     * <p>
     * This endpoint retrieves the information of multiple users specified by their IDs.
     * </p>
     *
     * @param ids the list of user IDs to retrieve.
     * @return a response containing the list of user information.
     */
    @GetMapping
    @Operation(summary = "Get users by IDs",
            description = "Retrieves information for multiple users based on their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "One or more users not found"),
    })
    public ResponseEntity<List<UserResponse>> getUsersByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(userService.getUsersById(ids));
    }

    /**
     * Retrieve user information by email.
     *
     * <p>
     * This endpoint retrieves the information of a user specified by their email.
     * </p>
     *
     * @param email the email of the user to retrieve.
     * @return a response containing the user information.
     */
    @GetMapping("/by-email")
    @Operation(summary = "Get user by email",
            description = "Retrieves user information for the specified email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Update user information by ID.
     *
     * <p>
     * This endpoint updates the information of a user specified by their ID.
     * </p>
     *
     * @param id      the ID of the user to update.
     * @param request the updated user information.
     * @return a response containing the updated user information.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID",
            description = "Updates the information of a specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Delete user by ID.
     *
     * <p>
     * This endpoint deletes a user specified by their ID.
     * </p>
     *
     * @param id the ID of the user to delete.
     * @return a response indicating the deletion status.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID",
            description = "Deletes the user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
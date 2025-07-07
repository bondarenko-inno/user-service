package org.ebndrnk.userservice.exception.dto;

/**
 * UserServiceException
 * <p>
 * Base class for all custom exceptions in the User Service module.
 * <p>
 * This runtime exception is intended to be extended by specific
 * custom exceptions to represent various error scenarios in the service.
 * It allows you to encapsulate domain-specific error information
 * and propagate meaningful error messages to higher layers
 * or to the API response.
 * <p>
 * Example usage:
 * <pre>
 * throw new UserNotFoundException("User with id 10 not found");
 * </pre>
 */
public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}

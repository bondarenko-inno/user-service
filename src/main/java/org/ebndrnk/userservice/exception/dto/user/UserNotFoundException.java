package org.ebndrnk.userservice.exception.dto.user;

import org.ebndrnk.userservice.exception.dto.UserServiceException;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

package org.ebndrnk.userservice.exception.dto.user;

import org.ebndrnk.userservice.exception.dto.UserServiceException;

public class DuplicateEmailException extends UserServiceException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}


package org.ebndrnk.userservice.exception.dto.card;

import org.ebndrnk.userservice.exception.dto.UserServiceException;

public class ExpiredCardException extends UserServiceException {
    public ExpiredCardException(String message) {
        super(message);
    }
}

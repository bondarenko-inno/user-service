package org.ebndrnk.userservice.exception.dto.card;

import org.ebndrnk.userservice.exception.dto.UserServiceException;

public class DuplicateCardNumberException extends UserServiceException {
    public DuplicateCardNumberException(String message) {
        super(message);
    }
}

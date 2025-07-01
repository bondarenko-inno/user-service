package org.ebndrnk.userservice.exception.dto.card;

import org.ebndrnk.userservice.exception.dto.UserServiceException;

public class CardInfoNotFoundException extends UserServiceException {
    public CardInfoNotFoundException(String message) {
        super(message);
    }
}

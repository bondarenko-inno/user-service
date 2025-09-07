package org.ebndrnk.userservice.exception.card;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class DuplicateCardNumberException extends BaseServiceException {
    public DuplicateCardNumberException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_CARD_NUMBER");
    }
}

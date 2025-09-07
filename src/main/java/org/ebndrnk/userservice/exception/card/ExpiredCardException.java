package org.ebndrnk.userservice.exception.card;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class ExpiredCardException extends BaseServiceException {
    public ExpiredCardException(String message) {
        super(message, HttpStatus.GONE, "EXPIRED_CARD_NUMBER");
    }
}

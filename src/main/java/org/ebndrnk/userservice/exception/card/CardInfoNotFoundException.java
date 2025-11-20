package org.ebndrnk.userservice.exception.card;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class CardInfoNotFoundException extends BaseServiceException {
    public CardInfoNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "CARD_INFO_NOT_FOUND");
    }
}

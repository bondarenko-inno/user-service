package org.ebndrnk.userservice.exception.dto.card;

public class DuplicateCardNumberException extends RuntimeException {
    public DuplicateCardNumberException(String message) {
        super(message);
    }
}

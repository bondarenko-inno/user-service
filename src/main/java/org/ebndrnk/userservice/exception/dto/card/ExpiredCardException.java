package org.ebndrnk.userservice.exception.dto.card;

public class ExpiredCardException extends RuntimeException {
    public ExpiredCardException() {
        super("Cannot register an expired card");
    }
}

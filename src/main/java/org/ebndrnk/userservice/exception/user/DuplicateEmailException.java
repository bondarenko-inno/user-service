package org.ebndrnk.userservice.exception.user;

import org.ebndrnk.common.common.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseServiceException {
    public DuplicateEmailException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_EMAIL");
    }
}


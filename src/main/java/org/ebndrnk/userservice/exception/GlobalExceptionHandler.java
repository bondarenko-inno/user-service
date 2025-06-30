package org.ebndrnk.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.dto.card.CardInfoNotFoundException;
import org.ebndrnk.userservice.exception.dto.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("NoUserFoundException: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardInfoNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleCardInfoNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("NoCardInfoFoundException: {}", ex.getMessage(), ex);
        ErrorInfo errorInfo = new ErrorInfo(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }
}

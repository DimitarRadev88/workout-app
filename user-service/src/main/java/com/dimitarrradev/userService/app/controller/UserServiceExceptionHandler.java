package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.error.exception.*;
import com.dimitarrradev.userService.app.error.model.BindingExceptionModel;
import com.dimitarrradev.userService.app.error.model.ExceptionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(exception = UserNotFoundException.class)
    public ResponseEntity<ExceptionModel> handleUserNotFound(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(exception = UsernameAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handleUsernameAlreadyExists(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(exception = EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handleEmailAlreadyExists(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(exception = InvalidRequestBodyException.class)
    public ResponseEntity<BindingExceptionModel> handleInvalidRequestBody(InvalidRequestBodyException exception) {
        List<String> fieldNames = exception.getBindingResult().getFieldErrors().stream().map(FieldError::getField).toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BindingExceptionModel(
                        exception.getClass().getSimpleName(),
                        "Invalid request body fields!",
                        fieldNames
                        )
                );
    }

    @ExceptionHandler(exception = InvalidResetTokenException.class)
    public ResponseEntity<ExceptionModel> handleInvalidResetToken(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        new ExceptionModel(exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

}

package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.error.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(exception = UsernameAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handeUsernameOrEmailAlreadyExists(UsernameAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(exception = UserNotFoundException.class)
    public ResponseEntity<ExceptionModel> handeUsernameOrEmailAlreadyExists(UserNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(exception = EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handeUsernameOrEmailAlreadyExists(EmailAlreadyExistsException exception) {
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
    public ResponseEntity<BindingExceptionModel> handeInvalidRequestBody(InvalidRequestBodyException exception) {
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
    public ResponseEntity<ExceptionModel> handeUsernameOrEmailAlreadyExists(InvalidResetTokenException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        new ExceptionModel(exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

}

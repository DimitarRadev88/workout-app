package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.error.exception.EmailAlreadyExistsException;
import com.dimitarrradev.userService.app.error.exception.InvalidRequestBodyException;
import com.dimitarrradev.userService.app.error.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(exception = UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handeUsernameOrEmailAlreadyExists(UsernameAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(exception = EmailAlreadyExistsException.class)
    public ResponseEntity<String> handeUsernameOrEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(exception = InvalidRequestBodyException.class)
    public ResponseEntity<List<ObjectError>> handeInvalidRequestBody(InvalidRequestBodyException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getBindingResult().getAllErrors());
    }

}

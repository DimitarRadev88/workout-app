package com.dimitarrradev.userService.app.controller;

import com.dimitarrradev.userService.app.controller.binding.UserPasswordChangeModel;
import com.dimitarrradev.userService.app.error.exception.*;
import com.dimitarrradev.userService.app.error.model.BindingExceptionModel;
import com.dimitarrradev.userService.app.error.model.ExceptionModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceExceptionHandlerTests {

    @Autowired()
    private UserServiceExceptionHandler exceptionHandler;

    @Test
    void testHandleUserNotFoundReturnsCorrectResponse() {
        UserNotFoundException e = new UserNotFoundException("User does not exist");
        ExceptionModel expectedBody = new ExceptionModel(e.getClass().getSimpleName(), e.getMessage());

        ResponseEntity<ExceptionModel> response = exceptionHandler.handleUserNotFound(e);

        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void testHandleUsernameAlreadyExistsReturnsCorrectResponse() {
        UsernameAlreadyExistsException e = new UsernameAlreadyExistsException("Username already exists!");
        ExceptionModel expectedBody = new ExceptionModel(e.getClass().getSimpleName(), e.getMessage());

        ResponseEntity<ExceptionModel> response = exceptionHandler.handleUsernameAlreadyExists(e);

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void testHandleEmailAlreadyExistsReturnsCorrectResponse() {
        EmailAlreadyExistsException e = new EmailAlreadyExistsException("Email already exists!");
        ExceptionModel expectedBody = new ExceptionModel(e.getClass().getSimpleName(), e.getMessage());

        ResponseEntity<ExceptionModel> response = exceptionHandler.handleEmailAlreadyExists(e);

        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void testHandleInvalidResetTokenReturnsCorrectResponse() {
        InvalidResetTokenException e = new InvalidResetTokenException("Email already exists!");
        ExceptionModel expectedBody = new ExceptionModel(e.getClass().getSimpleName(), e.getMessage());

        ResponseEntity<ExceptionModel> response = exceptionHandler.handleInvalidResetToken(e);

        assertEquals(HttpStatus.NOT_ACCEPTABLE,response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void testHandleInvalidRequestBodyReturnsCorrectResponse() {
        UserPasswordChangeModel userPasswordChangeModel = new UserPasswordChangeModel(null, null, null);

        BindingResult bindingResult = new BeanPropertyBindingResult(userPasswordChangeModel, "userPasswordChangeModel");

        InvalidRequestBodyException invalidRequestBodyException = new InvalidRequestBodyException(bindingResult);
        List<String> fieldNames = bindingResult.getFieldErrors().stream().map(FieldError::getField).toList();
        BindingExceptionModel expected = new BindingExceptionModel(
                invalidRequestBodyException.getClass().getSimpleName(),
                "Invalid request body fields!",
                fieldNames
        );

        ResponseEntity<BindingExceptionModel> response = exceptionHandler.handleInvalidRequestBody(invalidRequestBodyException);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(expected, response.getBody());
    }


}

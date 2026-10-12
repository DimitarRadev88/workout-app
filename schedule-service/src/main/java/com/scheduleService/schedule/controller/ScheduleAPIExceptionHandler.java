package com.scheduleService.schedule.controller;

import com.scheduleService.schedule.error.exception.InvalidRequestBodyException;
import com.scheduleService.schedule.error.exception.ScheduleAlreadyExistsException;
import com.scheduleService.schedule.error.exception.ScheduleNotFoundException;
import com.scheduleService.schedule.error.model.BindingExceptionModel;
import com.scheduleService.schedule.error.model.ExceptionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ScheduleAPIExceptionHandler {

    @ExceptionHandler(ScheduleAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handleScheduleAlreadyExistsException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ExceptionModel> handleScheduleNotFoundExceptionException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(exception = InvalidRequestBodyException.class)
    public ResponseEntity<BindingExceptionModel> handleInvalidRequestBody(InvalidRequestBodyException exception) {
        List<String> fieldNames = exception.getBindingResult().getFieldErrors().stream().map(FieldError::getField).toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BindingExceptionModel(
                                exception.getClass().getSimpleName(),
                                "Invalid request body fields!",
                                fieldNames
                        )
                );
    }

}

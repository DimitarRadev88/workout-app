package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.error.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.error.exception.InvalidRequestBodyException;
import com.dimitarrradev.exercisesApi.error.model.BindingExceptionModel;
import com.dimitarrradev.exercisesApi.error.model.ExceptionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExerciseAPIExceptionHandler {

    @ExceptionHandler(ExerciseAlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handleExerciseAlreadyExistsException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ExceptionModel(
                                exception.getClass().getSimpleName(),
                                exception.getMessage())
                );
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<ExceptionModel> handleExerciseNotFoundException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
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

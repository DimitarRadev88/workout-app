package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.error.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.exercisesApi.error.exception.ExerciseNotFoundException;
import com.dimitarrradev.exercisesApi.error.model.ExceptionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

}

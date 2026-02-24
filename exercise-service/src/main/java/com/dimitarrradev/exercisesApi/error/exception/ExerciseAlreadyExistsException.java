package com.dimitarrradev.exercisesApi.error.exception;

public class ExerciseAlreadyExistsException extends RuntimeException {
    public ExerciseAlreadyExistsException(String message) {
        super(message);
    }
}

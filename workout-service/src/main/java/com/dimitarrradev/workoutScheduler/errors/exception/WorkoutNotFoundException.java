package com.dimitarrradev.workoutScheduler.errors.exception;

public class WorkoutNotFoundException extends RuntimeException {
    public WorkoutNotFoundException(String message) {
        super(message);
    }
}

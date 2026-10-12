package com.dimitarrradev.workoutScheduler.errors.exception;

public class ScheduleDoesNotExistException extends RuntimeException {
    public ScheduleDoesNotExistException(String message) {
        super(message);
    }
}

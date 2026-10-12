package com.dimitarrradev.workoutScheduler.workout.enums;

public enum TimeOfDay {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening");

    private final String value;

    TimeOfDay(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

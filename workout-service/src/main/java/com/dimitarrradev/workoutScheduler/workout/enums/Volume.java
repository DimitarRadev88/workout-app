package com.dimitarrradev.workoutScheduler.workout.enums;

public enum Volume {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String value;

    Volume(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

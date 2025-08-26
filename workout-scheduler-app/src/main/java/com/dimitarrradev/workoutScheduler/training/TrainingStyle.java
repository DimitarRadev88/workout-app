package com.dimitarrradev.workoutScheduler.training;

public enum TrainingStyle {
    WEIGHTLIFTING("Weightlifting"),
    BODYBUILDING("Bodybuilding"),
    CARDIO("Cardio"),
    FITNESS("Fitness");

    private final String name;

    TrainingStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

package com.dimitarrradev.exercisesApi.exercise.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetBodyPart {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    TRAPS("Traps"),
    TRICEPS("Triceps"),
    BICEPS("Biceps"),
    QUADS("Quads"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    SPINAL_ERECTORS("Spinal Erectors"),
    ABS("Abs"),
    OBLIQUES("Obliques"),
    CALVES("Calves"),
    FOREARMS("Forearms"),
    LEGS("Legs"),
    ADDUCTORS("Adductors"),
    ABDUCTORS("Abductors"),
    ALL("All");

    private final String name;

}

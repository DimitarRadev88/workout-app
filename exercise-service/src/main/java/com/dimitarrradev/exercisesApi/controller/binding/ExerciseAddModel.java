package com.dimitarrradev.exercisesApi.controller.binding;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;

public record ExerciseAddModel(
        String name,
        String description,
        TargetBodyPart bodyPart,
        Complexity complexity,
        MovementType movement
) {
}

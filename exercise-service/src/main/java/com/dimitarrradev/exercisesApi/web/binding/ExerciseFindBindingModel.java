package com.dimitarrradev.exercisesApi.web.binding;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;

public record ExerciseFindBindingModel (
        String name,
        TargetBodyPart targetBodyPart,
        Complexity complexity,
        MovementType movementType
) {
}

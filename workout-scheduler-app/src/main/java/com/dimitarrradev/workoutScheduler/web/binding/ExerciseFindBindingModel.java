package com.dimitarrradev.workoutScheduler.web.binding;

import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;

public record ExerciseFindBindingModel (
        String name,
        TargetBodyPart targetBodyPart,
        Complexity complexity,
        MovementType movementType
) {
}

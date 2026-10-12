package com.dimitarrradev.workoutScheduler.workout.service.dto;

import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.workoutExercise.dto.WorkoutExerciseServiceModel;
import com.dimitarrradev.workoutScheduler.workout.enums.WorkoutType;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutEditServiceModel(
        WorkoutType workoutType,
        List<TargetBodyPart> targetBodyParts,
        LocalDateTime workoutDateTime,
        List<WorkoutExerciseServiceModel> exercises
) {
}

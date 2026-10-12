package com.dimitarrradev.workoutScheduler.workout.service.dto;

import java.time.LocalDateTime;

public record WorkoutInScheduleViewModel(
        LocalDateTime workoutDateTime,
        Boolean isCompleted
) {
}

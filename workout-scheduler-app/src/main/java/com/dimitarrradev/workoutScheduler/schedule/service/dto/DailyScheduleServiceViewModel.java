package com.dimitarrradev.workoutScheduler.schedule.service.dto;

import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutServiceModel;

import java.util.List;

public record DailyScheduleServiceViewModel(
        List<WorkoutServiceModel> workouts
) {
}

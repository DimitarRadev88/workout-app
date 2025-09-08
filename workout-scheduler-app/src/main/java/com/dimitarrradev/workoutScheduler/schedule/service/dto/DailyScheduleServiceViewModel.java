package com.dimitarrradev.workoutScheduler.schedule.service.dto;

import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutServiceViewModel;

import java.util.List;

public record DailyScheduleServiceViewModel(
        List<WorkoutServiceViewModel> workouts
) {
}

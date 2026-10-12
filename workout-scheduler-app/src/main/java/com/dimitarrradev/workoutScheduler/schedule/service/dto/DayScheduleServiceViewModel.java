package com.dimitarrradev.workoutScheduler.schedule.service.dto;

import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutInScheduleViewModel;

import java.util.List;

public record DayScheduleServiceViewModel(
        List<WorkoutInScheduleViewModel> workouts
) {
}

package com.dimitarrradev.workoutScheduler.schedule.dto;

import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;

import java.time.DayOfWeek;
import java.util.Map;

public record WeeklyScheduleViewModel(
        Map<DayOfWeek, DailyScheduleServiceViewModel> schedules
) {
}

package com.dimitarrradev.workoutScheduler.schedule.service.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public record WeekScheduleViewModel(
        Map<DayOfWeek, List<DayScheduleServiceViewModel>> schedules
) {
}

package com.scheduleService.schedule.controller.binding;

import java.time.LocalDate;
import java.util.Map;

public record ScheduleAddModel(
        Long userId,
        Map<Long, Long> exercisesReps,
        LocalDate date
) {
}

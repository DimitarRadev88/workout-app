package com.scheduleService.schedule.controller.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ScheduleUpdateModel(
        @NotNull Map<Long, Long> exercisesReps,
        @NotNull LocalDate date,
        @NotNull Boolean isCompleted
) {
}

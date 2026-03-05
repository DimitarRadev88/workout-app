package com.scheduleService.schedule.error.model;

import java.util.List;

public record BindingExceptionModel(
        String exception,
        String message,
        List<String> fields
) {
}

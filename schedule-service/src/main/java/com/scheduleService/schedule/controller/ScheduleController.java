package com.scheduleService.schedule.controller;

import com.scheduleService.schedule.controller.binding.ScheduleAddModel;
import com.scheduleService.schedule.controller.binding.ScheduleUpdateModel;
import com.scheduleService.schedule.error.exception.InvalidRequestBodyException;
import com.scheduleService.schedule.model.ScheduleModel;
import com.scheduleService.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/add")
    void addSchedule(@Valid @RequestBody ScheduleAddModel scheduleAddModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        scheduleService.createSchedule(scheduleAddModel);
    }

    @GetMapping("/{userId}")
    ScheduleModel getSchedule(@PathVariable Long userId, @RequestParam LocalDate date) {
        return scheduleService.getSchedule(userId, date);
    }

    @GetMapping("/{userId}")
    CollectionModel<ScheduleModel> getAllSchedules(@PathVariable Long userId) {
        return scheduleService.getAll(userId);
    }

    @PatchMapping("/{userId}")
    ScheduleModel updateSchedule(@PathVariable Long userId, @Valid @RequestBody ScheduleUpdateModel scheduleUpdateModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        return scheduleService.update(userId, scheduleUpdateModel);
    }

}

package com.scheduleService.schedule.controller;

import com.scheduleService.schedule.controller.binding.ScheduleAddModel;
import com.scheduleService.schedule.model.ScheduleModel;
import com.scheduleService.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/add")
    void addSchedule(@RequestBody ScheduleAddModel scheduleAddModel) {
        scheduleService.createSchedule(scheduleAddModel);
    }

    @GetMapping("/{userId}")
    ResponseEntity<ScheduleModel> getSchedule(@PathVariable Long userId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(scheduleService.getSchedule(userId, date));

    }

}

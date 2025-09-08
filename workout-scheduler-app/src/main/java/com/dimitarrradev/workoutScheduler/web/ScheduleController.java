package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.schedule.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping()
    public String getSchedulePage() {
        return "schedules";
    }

    @GetMapping("/daily")
    public String getTodaySchedule() {
        return "daily-schedule";
    }

}

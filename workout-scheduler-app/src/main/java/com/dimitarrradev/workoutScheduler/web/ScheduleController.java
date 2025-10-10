package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.schedule.dto.WeeklyScheduleViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.ScheduleService;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping()
    public String getSchedulePage() {
        return "schedules";
    }

    @GetMapping("/daily")
    public String getTodaySchedule(Model model, Principal principal) {

        DailyScheduleServiceViewModel dailySchedule = scheduleService.getDailySchedule(principal.getName());

        model.addAttribute("dailySchedule", dailySchedule);

        return "schedule-daily";
    }

    @GetMapping("/weekly")
    public String getWeeklySchedule(Model model, Principal principal) {
        WeeklyScheduleViewModel weeklySchedule = scheduleService.getWeeklySchedule(principal.getName());

        return "schedule-weekly";
    }

}

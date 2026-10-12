package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.schedule.service.dto.WeekScheduleViewModel;
import com.dimitarrradev.workoutScheduler.schedule.service.DayScheduleService;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DayScheduleServiceViewModel;
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

    private final DayScheduleService dayScheduleService;

    @GetMapping()
    public String getSchedulePage() {
        return "schedules";
    }

    @GetMapping("/daily")
    public String getTodaySchedule(Model model, Principal principal) {

        DayScheduleServiceViewModel dailySchedule = dayScheduleService.getDailySchedule(principal.getName());

        model.addAttribute("dailySchedule", dailySchedule);

        return "schedule-daily";
    }

    @GetMapping("/weekly")
    public String getWeeklySchedule(Model model, Principal principal) {
        WeekScheduleViewModel weeklySchedule = dayScheduleService.getWeekSchedule(principal.getName());

        return "schedule-weekly";
    }

}

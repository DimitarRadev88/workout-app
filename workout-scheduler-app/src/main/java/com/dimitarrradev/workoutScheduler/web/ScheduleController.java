package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.schedule.service.ScheduleService;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String getTodaySchedule(Model model, Authentication authentication) {

        DailyScheduleServiceViewModel dailySchedule = scheduleService.getDailySchedule(authentication.name());

        model.addAttribute("dailySchedule", dailySchedule);

        return "schedule-daily";
    }

}

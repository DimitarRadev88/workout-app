package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.schedule.service.ScheduleService;
import com.dimitarrradev.workoutScheduler.schedule.service.dto.DailyScheduleServiceViewModel;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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

}

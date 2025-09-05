package com.dimitarrradev.workoutScheduler.schedule.service;

import com.dimitarrradev.workoutScheduler.schedule.dao.DayScheduleRepository;
import com.dimitarrradev.workoutScheduler.schedule.dao.WeekScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final DayScheduleRepository dayScheduleRepository;
    private final WeekScheduleRepository weekScheduleRepository;

    public ScheduleService(DayScheduleRepository dayScheduleRepository, WeekScheduleRepository weekScheduleRepository) {
        this.dayScheduleRepository = dayScheduleRepository;
        this.weekScheduleRepository = weekScheduleRepository;
    }



}

package com.scheduleService.schedule.service;

import com.scheduleService.schedule.controller.binding.ScheduleAddModel;
import com.scheduleService.schedule.dao.ScheduleRepository;
import com.scheduleService.schedule.model.Schedule;
import com.scheduleService.schedule.model.ScheduleModel;
import com.scheduleService.schedule.util.ScheduleModelAssembler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleModelAssembler modelAssembler;

    public void createSchedule(ScheduleAddModel scheduleAddModel) {
        Schedule schedule = Schedule.builder()
                .userId(scheduleAddModel.userId())
                .exercises(scheduleAddModel.exercisesReps())
                .date(scheduleAddModel.date())
                .isDeleted(Boolean.FALSE)
                .build();

        scheduleRepository.saveAndFlush(schedule);
    }

    public ScheduleModel getSchedule(Long userId, LocalDate date) {
        return scheduleRepository.findByUserIdAndDate(userId, date)
                .map(modelAssembler::toModel)
                .orElse(null);
    }
}

package com.scheduleService.schedule.service;

import com.scheduleService.schedule.controller.binding.ScheduleAddModel;
import com.scheduleService.schedule.controller.binding.ScheduleUpdateModel;
import com.scheduleService.schedule.dao.ScheduleRepository;
import com.scheduleService.schedule.error.exception.ScheduleAlreadyExistsException;
import com.scheduleService.schedule.error.exception.ScheduleNotFoundException;
import com.scheduleService.schedule.model.Schedule;
import com.scheduleService.schedule.model.ScheduleModel;
import com.scheduleService.schedule.util.ScheduleModelAssembler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleModelAssembler modelAssembler;

    public void createSchedule(ScheduleAddModel scheduleAddModel) {
        if (scheduleRepository.existsByDateAndUserId(scheduleAddModel.date(), scheduleAddModel.userId())) {
            throw new ScheduleAlreadyExistsException("Schedule for this date already exists");
        }

        Schedule schedule = Schedule.builder()
                .userId(scheduleAddModel.userId())
                .exercises(scheduleAddModel.exercisesReps())
                .date(scheduleAddModel.date())
                .isDeleted(Boolean.FALSE)
                .isCompleted(scheduleAddModel.isCompleted())
                .build();

        scheduleRepository.saveAndFlush(schedule);
    }

    public ScheduleModel getSchedule(Long userId, LocalDate date) {
        return modelAssembler.toModel(findSchedule(userId, date));
    }

    public CollectionModel<ScheduleModel> getAll(Long userId) {
        return modelAssembler.toCollectionModel(scheduleRepository.findAllByUserId(userId));
    }

    public ScheduleModel update(Long userId, ScheduleUpdateModel scheduleUpdateModel) {
        Schedule schedule = findSchedule(userId, scheduleUpdateModel.date());

        schedule.setExercises(scheduleUpdateModel.exercisesReps());
        schedule.setIsCompleted(scheduleUpdateModel.isCompleted());

        Schedule saved = scheduleRepository.saveAndFlush(schedule);

        return modelAssembler.toModel(saved);
    }

    private Schedule findSchedule(Long userId, LocalDate date) {
        return scheduleRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule or user id does not exist"));
    }
}

package com.scheduleService.schedule.util;

import com.scheduleService.schedule.model.Schedule;
import com.scheduleService.schedule.model.ScheduleModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ScheduleModelAssembler implements RepresentationModelAssembler<Schedule, ScheduleModel> {

    @Override
    public ScheduleModel toModel(Schedule entity) {
        return new ScheduleModel(
                entity.getId(),
                entity.getUserId(),
                entity.getExercises(),
                entity.getDate(),
                entity.getIsCompleted(),
                entity.getIsDeleted()
        );

    }
}

package com.scheduleService.schedule.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "schedule")
public class ScheduleModel extends RepresentationModel<ScheduleModel> {
    private Long id;
    private Long userId;
    private Map<Long, Long> exercisesReps;
    private LocalDate date;
    private Boolean isCompleted;
    private Boolean isDeleted;
}
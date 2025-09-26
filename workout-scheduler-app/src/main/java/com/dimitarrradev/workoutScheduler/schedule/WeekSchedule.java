package com.dimitarrradev.workoutScheduler.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "week_schedules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WeekSchedule extends Schedule {

    @OneToMany(mappedBy = "weekSchedule")
    private List<DaySchedule> daySchedules;

}

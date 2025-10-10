package com.dimitarrradev.workoutScheduler.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "week_schedules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WeekSchedule extends Schedule {

    @OneToMany(mappedBy = "weekSchedule")
    private List<DaySchedule> daySchedules = new ArrayList<>();

}

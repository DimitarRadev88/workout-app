package com.dimitarrradev.workoutScheduler.schedule;

import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "day_schedules")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DaySchedule extends Schedule {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "daySchedule")
    private List<Workout> workouts;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_schedule_id")
    private WeekSchedule weekSchedule;
    @Basic
    private Boolean isCompleted;
}


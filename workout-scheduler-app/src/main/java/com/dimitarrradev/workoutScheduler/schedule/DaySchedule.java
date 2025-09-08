package com.dimitarrradev.workoutScheduler.schedule;

import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "day_schedules")
public class DaySchedule extends Schedule {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "day_schedule_id")
    private List<Workout> workout;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_schedule_id")
    private WeekSchedule weekSchedule;
    @Basic
    private Boolean isCompleted;

    public List<Workout> getWorkout() {
        return workout;
    }

    public void setWorkout(List<Workout> workout) {
        this.workout = workout;
    }

    public WeekSchedule getWeekSchedule() {
        return weekSchedule;
    }

    public void setWeekSchedule(WeekSchedule weekSchedule) {
        this.weekSchedule = weekSchedule;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

}

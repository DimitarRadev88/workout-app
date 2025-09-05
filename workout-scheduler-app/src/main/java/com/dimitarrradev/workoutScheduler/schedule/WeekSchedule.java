package com.dimitarrradev.workoutScheduler.schedule;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "week_schedules")
public class WeekSchedule extends Schedule {

    @OneToMany(mappedBy = "weekSchedule")
    private List<DaySchedule> daySchedules;

    public List<DaySchedule> getDaySchedules() {
        return daySchedules;
    }

    public void setDaySchedules(List<DaySchedule> daySchedules) {
        this.daySchedules = daySchedules;
    }

}

package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.user.User;
import com.dimitarrradev.workoutScheduler.workout.enums.Intensity;
import com.dimitarrradev.workoutScheduler.workout.enums.TimeOfDay;
import com.dimitarrradev.workoutScheduler.workout.enums.Volume;
import jakarta.persistence.*;
import org.springframework.beans.propertyeditors.LocaleEditor;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Entity
@Table(name = "workouts")
public class Workout extends BaseEntity {
    @Basic
    private LocalDate date;
    @OneToMany
    @MapKeyJoinColumn(name = "exercise_id")
    @JoinColumn(name = "set_id")
    @JoinTable(
            name = "workouts_exercises",
            joinColumns = @JoinColumn(name = "workout_id")
    )
    private Map<Exercise, TrainingSet> exerciseTrainingSets;
    @ManyToOne
    private Program program;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Intensity intensity;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Volume volume;
    @ManyToOne
    private User user;
    @ManyToOne
    @JoinColumn(name = "day_schedule_id")
    private DaySchedule daySchedule;
    @Column(name = "time_of_day", nullable = false)
    @Enumerated(EnumType.STRING)
    private TimeOfDay timeOfDay;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<Exercise, TrainingSet> getExerciseTrainingSets() {
        return exerciseTrainingSets;
    }

    public void setExerciseTrainingSets(Map<Exercise, TrainingSet> exerciseTrainingSets) {
        this.exerciseTrainingSets = exerciseTrainingSets;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DaySchedule getDaySchedule() {
        return daySchedule;
    }

    public void setDaySchedule(DaySchedule daySchedule) {
        this.daySchedule = daySchedule;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

}

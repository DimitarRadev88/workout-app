package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.schedule.DaySchedule;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
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
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "workout")
    private List<DaySchedule> daySchedules;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<DaySchedule> getDaySchedules() {
        return daySchedules;
    }

    public void setDaySchedules(List<DaySchedule> daySchedules) {
        this.daySchedules = daySchedules;
    }
}

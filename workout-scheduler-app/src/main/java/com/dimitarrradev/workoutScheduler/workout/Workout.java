package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import com.dimitarrradev.workoutScheduler.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}

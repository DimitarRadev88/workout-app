package com.dimitarrradev.workoutScheduler.trainingSet;

import com.dimitarrradev.workoutScheduler.BaseEntity;
import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "sets")
public class TrainingSet extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
    @Column(name = "min_reps", nullable = false)
    private Integer minReps;
    @Column(name = "max_reps", nullable = false)
    private Integer maxReps;
    @Basic
    private Integer actualReps;
    @Basic
    private Double weight;
    @Column(nullable = false)
    private Integer rest;
    @Column(nullable = false)
    private Integer count;

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getMinReps() {
        return minReps;
    }

    public void setMinReps(Integer minReps) {
        this.minReps = minReps;
    }

    public Integer getMaxReps() {
        return maxReps;
    }

    public void setMaxReps(Integer maxReps) {
        this.maxReps = maxReps;
    }

    public Integer getActualReps() {
        return actualReps;
    }

    public void setActualReps(Integer actualReps) {
        this.actualReps = actualReps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

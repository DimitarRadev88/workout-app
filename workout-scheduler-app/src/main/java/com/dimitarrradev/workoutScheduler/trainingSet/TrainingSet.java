package com.dimitarrradev.workoutScheduler.trainingSet;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

}

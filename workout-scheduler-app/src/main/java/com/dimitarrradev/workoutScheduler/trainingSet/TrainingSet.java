package com.dimitarrradev.workoutScheduler.trainingSet;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "sets")
public class TrainingSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exercise_target_min_reps")
    @ElementCollection
    @CollectionTable(name = "set_min_reps", joinColumns = @JoinColumn(name = "set_id"))
    @MapKeyColumn(name = "exercise_id")
    private Map<Exercise, Integer> exerciseMinRepsMap;
    @ElementCollection
    @CollectionTable(name = "set_max_reps", joinColumns = @JoinColumn(name = "set_id"))
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "exercise_target_max_reps")
    private Map<Exercise, Integer> exerciseMaxRepsMap;
    @ElementCollection
    @CollectionTable(name = "set_rest", joinColumns = @JoinColumn(name = "set_id"))
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "exercise_target_rest")
    private Map<Exercise, Integer> exerciseRestMap;
    @Basic
    private Integer actualReps;
    @Basic
    private Double weight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<Exercise, Integer> getExerciseMinRepsMap() {
        return exerciseMinRepsMap;
    }

    public void setExerciseMinRepsMap(Map<Exercise, Integer> exerciseMinRepsMap) {
        this.exerciseMinRepsMap = exerciseMinRepsMap;
    }

    public Map<Exercise, Integer> getExerciseMaxRepsMap() {
        return exerciseMaxRepsMap;
    }

    public void setExerciseMaxRepsMap(Map<Exercise, Integer> exerciseMaxRepsMap) {
        this.exerciseMaxRepsMap = exerciseMaxRepsMap;
    }

    public Map<Exercise, Integer> getExerciseRestMap() {
        return exerciseRestMap;
    }

    public void setExerciseRestMap(Map<Exercise, Integer> exerciseRestMap) {
        this.exerciseRestMap = exerciseRestMap;
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
}

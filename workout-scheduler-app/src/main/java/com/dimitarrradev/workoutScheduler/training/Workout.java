package com.dimitarrradev.workoutScheduler.training;

import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ElementCollection
    @CollectionTable(name = "exercise_min_reps", joinColumns = @JoinColumn(name = "workout_id"))
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "exercise_target_min_reps")
    private Map<Exercise, Integer> exerciseMinRepsMap;
    @ElementCollection
    @CollectionTable(name = "exercise_max_reps", joinColumns = @JoinColumn(name = "workout_id"))
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "exercise_target_max_reps")
    private Map<Exercise, Integer> exerciseMaxRepsMap;
    @ElementCollection
    @CollectionTable(name = "exercise_rest", joinColumns = @JoinColumn(name = "workout_id"))
    @MapKeyColumn(name = "exercise_id")
    @Column(name = "exercise_target_rest")
    private Map<Exercise, Integer> exerciseRestMap;

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
}

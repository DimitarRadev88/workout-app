package com.dimitarrradev.workoutScheduler.workout;

import com.dimitarrradev.workoutScheduler.exercise.Exercise;
import com.dimitarrradev.workoutScheduler.program.Program;
import com.dimitarrradev.workoutScheduler.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Basic
    private LocalDate date;
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
    @ManyToOne
    private Program program;
    @ManyToMany(mappedBy = "workouts")
    private List<User> users;

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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}

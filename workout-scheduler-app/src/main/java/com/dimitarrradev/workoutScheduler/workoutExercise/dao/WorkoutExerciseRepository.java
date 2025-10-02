package com.dimitarrradev.workoutScheduler.workoutExercise.dao;

import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    Optional<WorkoutExercise> findByIdAndWorkout_Id(Long id, Long workoutId);
}

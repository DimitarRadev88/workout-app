package com.dimitarrradev.workoutScheduler.workoutExercise.dao;

import com.dimitarrradev.workoutScheduler.workoutExercise.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

    import java.util.Optional;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    boolean existsByIdAndWorkout_User_Username(Long id, String username);

    Optional<WorkoutExercise> findByIdAndWorkout_IdAndWorkout_User_Username(Long id, Long workoutId, String username);
}

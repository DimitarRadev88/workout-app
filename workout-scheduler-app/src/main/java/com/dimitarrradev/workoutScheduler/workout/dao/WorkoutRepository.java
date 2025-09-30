package com.dimitarrradev.workoutScheduler.workout.dao;

import aj.org.objectweb.asm.commons.Remapper;
import com.dimitarrradev.workoutScheduler.workout.Workout;
import com.dimitarrradev.workoutScheduler.workout.service.dto.WorkoutEditServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    Optional<Workout> findWorkoutById(Long id);

    List<Workout> findAllByUser_UsernameOrderByWorkoutDateTimeDesc(String userUsername);

    Optional<Workout> findWorkoutByIdAndUser_Username(long id, String userUsername);
}

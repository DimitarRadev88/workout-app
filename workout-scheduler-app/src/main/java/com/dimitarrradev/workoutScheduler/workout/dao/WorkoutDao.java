package com.dimitarrradev.workoutScheduler.workout.dao;

import com.dimitarrradev.workoutScheduler.workout.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutDao extends JpaRepository<Workout, Long> {
}

package com.dimitarrradev.workoutScheduler.training.dao;

import com.dimitarrradev.workoutScheduler.training.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutDao extends JpaRepository<Workout, Long> {
}

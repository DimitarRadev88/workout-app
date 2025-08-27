package com.dimitarrradev.workoutScheduler.training.dao;

import com.dimitarrradev.workoutScheduler.training.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDao extends JpaRepository<Exercise, Long> {
}

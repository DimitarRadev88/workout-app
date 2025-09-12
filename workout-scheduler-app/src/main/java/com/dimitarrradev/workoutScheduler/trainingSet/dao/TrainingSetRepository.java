package com.dimitarrradev.workoutScheduler.trainingSet.dao;

import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSetRepository extends JpaRepository<TrainingSet, Long> {
    int findCountByWorkoutIdAndExerciseName(Long id, String name);
}

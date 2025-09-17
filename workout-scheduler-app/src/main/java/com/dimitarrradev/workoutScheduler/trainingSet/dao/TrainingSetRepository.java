package com.dimitarrradev.workoutScheduler.trainingSet.dao;

import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSetRepository extends JpaRepository<TrainingSet, Long> {
    long countTrainingSetByWorkoutIdAndExerciseNameAndMinRepsAndMaxRepsAndWeight(Long workoutID, String exerciseName, Integer minReps, Integer maxReps, Double weight);
}

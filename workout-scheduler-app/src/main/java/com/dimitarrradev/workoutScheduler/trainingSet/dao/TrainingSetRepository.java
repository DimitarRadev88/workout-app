package com.dimitarrradev.workoutScheduler.trainingSet.dao;

import com.dimitarrradev.workoutScheduler.trainingSet.TrainingSet;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

@Resource
public interface TrainingSetRepository extends JpaRepository<TrainingSet, Long> {

}

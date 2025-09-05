package com.dimitarrradev.workoutScheduler.trainingSet.service;

import com.dimitarrradev.workoutScheduler.trainingSet.dao.TrainingSetRepository;
import org.springframework.stereotype.Service;

@Service
public class TrainingSetService {

    private final TrainingSetRepository trainingSetRepository;

    public TrainingSetService(TrainingSetRepository trainingSetRepository) {
        this.trainingSetRepository = trainingSetRepository;
    }

}

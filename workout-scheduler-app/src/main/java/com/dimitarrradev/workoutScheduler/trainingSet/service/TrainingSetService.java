package com.dimitarrradev.workoutScheduler.trainingSet.service;

import com.dimitarrradev.workoutScheduler.trainingSet.dao.TrainingSetDao;
import org.springframework.stereotype.Service;

@Service
public class TrainingSetService {

    private final TrainingSetDao trainingSetDao;

    public TrainingSetService(TrainingSetDao trainingSetDao) {
        this.trainingSetDao = trainingSetDao;
    }

}

package com.dimitarrradev.workoutScheduler.workout.service;

import com.dimitarrradev.workoutScheduler.workout.dao.WorkoutRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

}

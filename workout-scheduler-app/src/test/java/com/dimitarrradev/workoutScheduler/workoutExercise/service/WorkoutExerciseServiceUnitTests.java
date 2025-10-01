package com.dimitarrradev.workoutScheduler.workoutExercise.service;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import com.dimitarrradev.workoutScheduler.workoutExercise.dao.WorkoutExerciseRepository;
import com.dimitarrradev.workoutScheduler.web.binding.ExerciseWorkoutExerciseBindingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkoutExerciseServiceUnitTests {

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;
    @Mock
    private ExerciseService exerciseService;
    @InjectMocks
    private WorkoutExerciseService workoutExerciseService;

//    @Test
//    void testCreateWorkoutExerciseCreatesNewWorkoutExerciseWithCorrectData() {
//        ExerciseWorkoutExerciseBindingModel bindingModel = new ExerciseWorkoutExerciseBindingModel  (1L)
//    }

}
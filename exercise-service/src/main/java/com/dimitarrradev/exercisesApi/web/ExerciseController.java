package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    private ResponseEntity<ExerciseViewModel> getExercise(@PathVariable Long id) {
        ExerciseViewModel exercise = exerciseService.getExerciseView(id);

        return ResponseEntity
                .ok()
                .body(exercise);
    }

    @PostMapping("/add")
    private ResponseEntity<Long> addExercise(@RequestBody ExerciseAddBindingModel exerciseAdd) {
        Long id = exerciseService.addExerciseForReview(exerciseAdd);

        return ResponseEntity
                .created(URI.create("/exercises/" + id))
                .body(id);
    }

}

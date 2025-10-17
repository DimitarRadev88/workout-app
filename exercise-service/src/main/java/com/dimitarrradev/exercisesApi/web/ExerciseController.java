package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseFindViewModel;
import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseViewModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseFindBindingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PutMapping("/edit/{id}")
    private ResponseEntity<Long> addExercise(@RequestBody ExerciseEditBindingModel exerciseEdit) {
        Long id = exerciseService.editExercise(exerciseEdit);

        return ResponseEntity
                .created(URI.create("/exercises/" + id))
                .body(id);
    }

    @GetMapping("/find")
    private ResponseEntity<Page<ExerciseFindViewModel>> findExercise(
            @RequestBody ExerciseFindBindingModel exerciseFind,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<ExerciseFindViewModel> exercisesPage = exerciseService.findActiveExercisesPage(exerciseFind, pageNumber, pageSize, sortDirection);

        return ResponseEntity
                .ok()
                .body(exercisesPage);
    }

}

package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseModel> getExercise(@PathVariable Long id) {
        return ResponseEntity
                .ok(exerciseService.getExerciseModel(id));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addExercise(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String bodyPart,
            @RequestParam String complexity,
            @RequestParam String movement
    ) {
        Long id = exerciseService.addExerciseForReview(name, description, bodyPart, complexity, movement);

        return ResponseEntity
                .created(URI.create("/exercises/" + id))
                .header("Link", "")
                .contentType(MediaType.APPLICATION_JSON)
                .body("Exercise " + id + " added successfully!");
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<Void> editExercise(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {

        exerciseService.editExercise(id, name, description);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/find")
    public CollectionModel<ExerciseModel> findExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String complexity,
            @RequestParam(required = false) String movement,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {

        return exerciseService.findExercisesPage(name, target, complexity, movement, page, size, orderBy);
    }

    @GetMapping("/")
    public CollectionModel<ExerciseModel> getExercisesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {

        return exerciseService.getExercises(page, size, orderBy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageUrl>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getImages(id));
    }

}

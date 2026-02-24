package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrlModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ExerciseModel getExercise(@PathVariable Long id) {
        return exerciseService.getExerciseModel(id);
    }

    @PostMapping("/add")
    public ExerciseModel addExercise(@RequestBody ExerciseAddModel addModel) {
        return exerciseService.addExercise(addModel);
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

        return exerciseService.findExercises(name, target, complexity, movement, page, size, orderBy);
    }

    @GetMapping("/")
    public PagedModel<ExerciseModel> getExercisesPage(
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
    public CollectionModel<ImageUrlModel> getImages(@PathVariable Long id) {
        return exerciseService.getImages(id);
    }

    @GetMapping("/{id}/images/{imageId}")
    public ImageUrlModel getImage(@PathVariable Long id, @PathVariable Long imageId) {
        return exerciseService.getImage(id, imageId);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> addImage(@PathVariable Long id, @RequestParam String url) {
        Long imageId = exerciseService.addImage(id, url);

        return ResponseEntity
                .created(linkTo(methodOn(ExerciseController.class).getImage(id, imageId)).withSelfRel().toUri())
                .build();
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        exerciseService.deleteImage(id, imageId);

        return ResponseEntity.noContent().build();
    }

}

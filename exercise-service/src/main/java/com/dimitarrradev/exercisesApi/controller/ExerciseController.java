package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlAddModel;
import com.dimitarrradev.exercisesApi.error.exception.InvalidRequestBodyException;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrlModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public ExerciseModel addExercise(
            @RequestBody @Valid ExerciseAddModel addModel,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        return exerciseService.addExercise(addModel);
    }
    @PatchMapping("/{id}")
    public ExerciseModel editExercise(
            @PathVariable Long id,
            @RequestBody @Valid ExerciseEditModel editModel,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestBodyException(bindingResult);
        }

        return exerciseService.editExercise(id, editModel);
    }

    @GetMapping("/find")
    public PagedModel<ExerciseModel> findExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String complexity,
            @RequestParam(required = false) String movement,
            @RequestParam(defaultValue = "1") int page,
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
    public ImageUrlModel addImage(@PathVariable Long id, @RequestBody ImageUrlAddModel urlModel) {
        return exerciseService.addImage(id, urlModel);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        exerciseService.deleteImage(id, imageId);

        return ResponseEntity.noContent().build();
    }

}

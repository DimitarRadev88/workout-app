package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlsAddModel;
import com.dimitarrradev.exercisesApi.error.exception.InvalidRequestBodyException;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrlModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
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

    @GetMapping("/search")
    public PagedModel<ExerciseModel> searchExercises(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "ALL") TargetBodyPart target,
            @RequestParam(required = false, defaultValue = "ALL") Complexity complexity,
            @RequestParam(required = false, defaultValue = "ALL") MovementType movement,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {

        return exerciseService.searchExercises(name, target, complexity, movement, page, size, orderBy);
    }

    @DeleteMapping("/{id}")
    public ExerciseModel deleteExercise(@PathVariable Long id) {
        return exerciseService.deleteExercise(id);
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
    public CollectionModel<ImageUrlModel> addImages(@PathVariable Long id, @RequestBody ImageUrlsAddModel urlsModel) {
        return exerciseService.addImages(id, urlsModel);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ImageUrlModel deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        return exerciseService.deleteImage(id, imageId);
    }

}

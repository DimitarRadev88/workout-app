package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseFindBindingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    private ResponseEntity<ExerciseModel> getExercise(@PathVariable Long id) {
        ExerciseModel exercise = exerciseService.getExerciseModel(id);

        exercise.add(Link.of(linkTo(ExerciseController.class).slash(exercise.getId()).withSelfRel().getHref()));

        return ResponseEntity
                .ok(exercise);
    }

    @PostMapping("/add")
    private ResponseEntity<String> addExercise(@RequestBody ExerciseAddBindingModel exerciseAdd) {
        Long id = exerciseService.addExerciseForReview(exerciseAdd);

        return ResponseEntity
                .created(URI.create("/exercises/" + id))
                .header("Link", "")
                .contentType(MediaType.APPLICATION_JSON)
                .body("Exercise " + id + " added successfully for review!");
    }

    @PatchMapping("/edit/{id}")
    private ResponseEntity<ExerciseModel> editExercise(@PathVariable Long id, @RequestBody ExerciseEditBindingModel exerciseEdit) {
        exerciseService.editExercise(id, exerciseEdit);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/find")
    private ResponseEntity<Page<ExerciseModel>> findExercise(
            @RequestBody ExerciseFindBindingModel exerciseFind,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<ExerciseModel> exercisesPage = exerciseService.findActiveExercisesPage(exerciseFind, pageNumber, pageSize, sortDirection);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exercisesPage);
    }

    @GetMapping("/for-review/count")
    private ResponseEntity<Long> countForReview() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exerciseService.getExercisesForReviewCount());
    }

    @GetMapping("/for-review")
    private ResponseEntity<PagedModel<ExerciseModel>> getExercisesForReview(@RequestParam int page,
                                                                                         @RequestParam int size,
                                                                                         @RequestParam String orderBy) {

        Page<ExerciseModel> exercisesForReviewPage = exerciseService.getExercisesForReviewPage(page, size, orderBy);

        String linkHeader = "<http://localhost:8082/exercises/for-review?page=%d&size=%d&orderBy=%s>;, rel=\"%s\"";
        String link = "Link";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(link, String.format(linkHeader, page, size, orderBy, "self"))
                .header(link, String.format(linkHeader, 0, size, orderBy, "first"))
                .header(link, String.format(linkHeader, Math.max(0, page - 1), size, orderBy, "prev"))
                .header(link, String.format(linkHeader, Math.min(page + 1, exercisesForReviewPage.getTotalPages() - 1), size, orderBy, "next"))
                .header(link, String.format(linkHeader, exercisesForReviewPage.getTotalPages() - 1, size, orderBy, "last"))
                .body(new PagedModel<>(exercisesForReviewPage));
    }


}

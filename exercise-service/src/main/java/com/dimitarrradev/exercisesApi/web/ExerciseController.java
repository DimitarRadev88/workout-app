package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.dto.ExerciseModel;
import com.dimitarrradev.exercisesApi.exercise.dto.ExercisePagedModel;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
        
        exercise.add(linkTo(ExerciseController.class).slash(exercise.getId()).withSelfRel());

        return ResponseEntity
                .ok(exercise);
    }

    @PostMapping("/add")
    private ResponseEntity<String> addExercise(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String bodyPart,
            @RequestParam String addedBy,
            @RequestParam String complexity,
            @RequestParam String movement
    ) {
        Long id = exerciseService.addExerciseForReview(name, description, bodyPart, addedBy, complexity, movement);

        return ResponseEntity
                .created(URI.create("/exercises/" + id))
                .header("Link", "")
                .contentType(MediaType.APPLICATION_JSON)
                .body("Exercise " + id + " added successfully for review!");
    }

    @PatchMapping("/edit/{id}")
    private ResponseEntity<Void> editExercise(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean approved
    ) {


        exerciseService.editExercise(id, name, description, approved);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/find")
    private ResponseEntity<Page<ExerciseModel>> findExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String complexity,
            @RequestParam(required = false) String movement,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {

        Page<ExerciseModel> exercisesPage = exerciseService.findActiveExercisesPage(name, target, complexity, movement, page, size, orderBy);

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
    private ResponseEntity<ExercisePagedModel> getExercisesForReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {

        Page<ExerciseModel> exercisesForReviewPage = exerciseService.getExercisesForReviewPage(page, size, orderBy);

        exercisesForReviewPage.forEach(e -> e.add(linkTo(ExerciseController.class).slash(e.getId()).withSelfRel()));

        String linkHeader = "<http://localhost:8082/exercises/for-review?page=%d&size=%d&orderBy=%s>;, rel=\"%s\"";
        String link = "Link";

        ExercisePagedModel pagedModel = new ExercisePagedModel();

        pagedModel.setContent(exercisesForReviewPage.getContent());

        String self = UriComponentsBuilder.fromUriString("for-review").queryParam("page", page).queryParam("size", size).queryParam("orderBy", orderBy).build().toString();
        String prev = UriComponentsBuilder.fromUriString("for-review").queryParam("page", Math.max(page, page - 1)).queryParam("size", size).queryParam("orderBy", orderBy).build().toString();
        String next = UriComponentsBuilder.fromUriString("for-review").queryParam("page", Math.min(page + 1, exercisesForReviewPage.getTotalPages() - 1)).queryParam("size", size).queryParam("orderBy", orderBy).build().toString();
        String first = UriComponentsBuilder.fromUriString("for-review").queryParam("page", Math.max(page, 0)).queryParam("size", size).queryParam("orderBy", orderBy).build().toString();
        String last = UriComponentsBuilder.fromUriString("for-review").queryParam("page", Math.max(page, exercisesForReviewPage.getTotalPages() - 1)).queryParam("size", size).queryParam("orderBy", orderBy).build().toString();

        pagedModel.add(linkTo(ExerciseController.class).slash(self).withSelfRel());
        pagedModel.add(linkTo(ExerciseController.class).slash(prev).withRel(LinkRelation.of("prev")));
        pagedModel.add(linkTo(ExerciseController.class).slash(next).withRel(LinkRelation.of("next")));
        pagedModel.add(linkTo(ExerciseController.class).slash(first).withRel(LinkRelation.of("first")));
        pagedModel.add(linkTo(ExerciseController.class).slash(last).withRel(LinkRelation.of("last")));



        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(link, String.format(linkHeader, page, size, orderBy, "self"))
                .header(link, String.format(linkHeader, 0, size, orderBy, "first"))
                .header(link, String.format(linkHeader, Math.max(0, page - 1), size, orderBy, "prev"))
                .header(link, String.format(linkHeader, Math.min(page + 1, exercisesForReviewPage.getTotalPages() - 1), size, orderBy, "next"))
                .header(link, String.format(linkHeader, exercisesForReviewPage.getTotalPages() - 1, size, orderBy, "last"))
                .body(pagedModel);
    }

}

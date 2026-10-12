package com.dimitarrradev.workoutScheduler.config.rest;

import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.dimitarrradev.workoutScheduler.exercise.service.ExercisePageImpl;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ExerciseRestClient {

    private final RestClient restClient;

    public ExerciseRestClient(@LoadBalanced RestClient.Builder builder, @Value("${exercises.api.url}") String apiUrl) {
        this.restClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .build();
    }

    public ResponseEntity<ExerciseViewModel> getExercise(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toEntity(ExerciseViewModel.class);
    }

    public ExerciseResponse addExercise(ExerciseAddBindingModel exerciseAdd) {
        return restClient.post()
                .uri("/add")
                .contentType(APPLICATION_JSON)
                .body(new ExerciseAddRequest(
                        exerciseAdd.exerciseName(),
                        exerciseAdd.description(),
                        exerciseAdd.bodyPart(),
                        exerciseAdd.complexity(),
                        exerciseAdd.movementType(),
                        exerciseAdd.addedBy()
                ))
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.CONFLICT), (request, response) -> {
                    throw new ExerciseAlreadyExistsException("Exercise with name " + exerciseAdd.exerciseName() + " already exists!");
                })
                .body(ExerciseResponse.class);
    }

    public ResponseEntity<ExerciseEditBindingModel> getExerciseEditBindingModel(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toEntity(ExerciseEditBindingModel.class);
    }

    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        Long id = exerciseEdit.id();
        ExerciseResponse current = getExerciseResponse(id);

        restClient.patch()
                .uri("/{id}", id)
                .contentType(APPLICATION_JSON)
                .body(new ExerciseEditRequest(
                        exerciseEdit.name(),
                        exerciseEdit.description(),
                        current.targetBodyPart(),
                        current.complexity(),
                        current.movementType()
                ))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toBodilessEntity();

        List<String> imageUrls = parseImageUrls(exerciseEdit.addImageUrls());
        if (!imageUrls.isEmpty()) {
            restClient.post()
                    .uri("/{id}/images", id)
                    .contentType(APPLICATION_JSON)
                    .body(new ImageUrlsAddRequest(imageUrls))
                    .retrieve()
                    .toBodilessEntity();
        }

        if (Boolean.TRUE.equals(exerciseEdit.approved()) && !Boolean.TRUE.equals(current.approved())) {
            approveExercise(id);
        }
    }

    public void approveExercise(Long id) {
        restClient.patch()
                .uri("/{id}/approve", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toBodilessEntity();
    }

    public ResponseEntity<Long> getExercisesForReviewCount() {
        return restClient.get()
                .uri("/for-review/count")
                .retrieve()
                .toEntity(Long.class);
    }

    public Page<ExerciseForReviewViewModel> getExercisesForReviewPage(int pageNumber, int pageSize, String sortDirection) {
        ExercisesForReviewResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/for-review")
                        .queryParam("page", pageNumber)
                        .queryParam("size", pageSize)
                        .queryParam("orderBy", sortDirection)
                        .build())
                .retrieve()
                .body(ExercisesForReviewResponse.class);

        List<ExerciseForReviewViewModel> content = response.embedded() == null ?
                List.of() :
                response.embedded().exerciseModelList();

        return new ExercisePageImpl<>(
                content,
                PageRequest.of(response.page().number(), response.page().size()),
                response.page().totalElements()
        );
    }

    private ExerciseResponse getExerciseResponse(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .body(ExerciseResponse.class);
    }

    private static List<String> parseImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.isBlank()) {
            return List.of();
        }

        return Arrays.stream(imageUrls.split("\\R"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .toList();
    }

    public record ExerciseResponse(
            Long id,
            String name,
            String description,
            TargetBodyPart targetBodyPart,
            Complexity complexity,
            MovementType movementType,
            Boolean approved,
            String addedBy
    ) {
    }

    private record ExerciseAddRequest(
            String name,
            String description,
            TargetBodyPart bodyPart,
            Complexity complexity,
            MovementType movement,
            String addedBy
    ) {
    }

    private record ExerciseEditRequest(
            String name,
            String description,
            TargetBodyPart bodyPart,
            Complexity complexity,
            MovementType movement
    ) {
    }

    private record ImageUrlsAddRequest(List<String> urls) {
    }

    private record ExercisesForReviewResponse(
            @JsonProperty("_embedded") Embedded embedded,
            PageMetadata page
    ) {
        private record Embedded(List<ExerciseForReviewViewModel> exerciseModelList) {
        }

        private record PageMetadata(int size, long totalElements, int totalPages, int number) {
        }
    }

}

package com.dimitarrradev.workoutScheduler.config.rest;

import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseViewModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseEditBindingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ExerciseRestClient {

    private final RestClient restClient;

    public ExerciseRestClient(RestClient.Builder builder, @Value("${exercises.api.url}") String apiUrl) {
        this.restClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .build();
    }

    public ResponseEntity<ExerciseViewModel> getExercise(Long id) {
        return restClient.get()
                .uri("/" + id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toEntity(ExerciseViewModel.class);
    }

    public ResponseEntity<String> addExercise(ExerciseAddBindingModel exerciseAdd) {
        return restClient.post()
                .uri("/add")
                .contentType(APPLICATION_JSON)
                .body(exerciseAdd)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<ExerciseEditBindingModel> getExerciseEditBindingModel(Long id) {
        return restClient.get()
                .uri("/edit/" + id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + id + " not found");
                })
                .toEntity(ExerciseEditBindingModel.class);
    }

    public void editExercise(ExerciseEditBindingModel exerciseEdit) {
        restClient.patch()
                .uri("/edit/" + exerciseEdit.id())
                .contentType(APPLICATION_JSON)
                .body(exerciseEdit)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExerciseNotFoundException("Exercise with id " + exerciseEdit.id() + " not found");
                })
                .toBodilessEntity();
    }

    public ResponseEntity<Long> getExercisesForReviewCount() {
        return restClient.get()
                .uri("/for-review/count")
                .retrieve()
                .toEntity(Long.class);
    }


}

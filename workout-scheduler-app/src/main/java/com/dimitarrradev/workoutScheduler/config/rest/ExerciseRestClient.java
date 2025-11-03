package com.dimitarrradev.workoutScheduler.config.rest;

import com.dimitarrradev.workoutScheduler.web.binding.ExerciseAddBindingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ExerciseRestClient {

    private final RestClient restClient;

    @Value("${exercises-api.url}")
    private String apiUrl;

    public ExerciseRestClient(RestClient.Builder builder) {
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .build();
    }

    public ResponseEntity<String> addExercise(ExerciseAddBindingModel exerciseAdd) {
        return restClient.post()
                .uri("/exercises/add")
                .contentType(APPLICATION_JSON)
                .body(exerciseAdd)
                .retrieve()
                .toEntity(String.class);
    }


}

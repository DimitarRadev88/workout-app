package com.dimitarrradev.workoutScheduler.config.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Configuration
public class RestClientConfig {


    @Bean(name = "exercisesRestClient")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8082/")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()))
                .build();
    }

}

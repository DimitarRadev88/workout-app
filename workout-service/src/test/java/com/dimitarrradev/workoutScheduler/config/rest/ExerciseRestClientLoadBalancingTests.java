package com.dimitarrradev.workoutScheduler.config.rest;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "eureka.client.enabled=false")
@ActiveProfiles("test")
class ExerciseRestClientLoadBalancingTests {

    private static final HttpServer exercisesService = startExercisesService();

    @Autowired
    private ExerciseRestClient exerciseRestClient;

    @DynamicPropertySource
    static void registerExercisesService(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.ExercisesService[0].uri",
                () -> "http://localhost:" + exercisesService.getAddress().getPort());
    }

    @AfterAll
    static void stopExercisesService() {
        exercisesService.stop(0);
    }

    @Test
    void requestsResolveExercisesServiceThroughDiscovery() {
        assertEquals(7L, exerciseRestClient.getExercisesForReviewCount().getBody());
    }

    private static HttpServer startExercisesService() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/api/exercises/for-review/count", exchange -> {
                byte[] body = "7".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}

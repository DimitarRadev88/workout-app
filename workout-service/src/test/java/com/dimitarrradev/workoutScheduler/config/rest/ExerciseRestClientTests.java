package com.dimitarrradev.workoutScheduler.config.rest;

import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseAlreadyExistsException;
import com.dimitarrradev.workoutScheduler.errors.exception.ExerciseNotFoundException;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseAddBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.binding.ExerciseEditBindingModel;
import com.dimitarrradev.workoutScheduler.exercise.dto.view.ExerciseForReviewViewModel;
import com.dimitarrradev.workoutScheduler.exercise.enums.Complexity;
import com.dimitarrradev.workoutScheduler.exercise.enums.MovementType;
import com.dimitarrradev.workoutScheduler.exercise.enums.TargetBodyPart;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
@ActiveProfiles("test")
class ExerciseRestClientTests {

    private static final String EXERCISE_JSON = """
            {"id":1,"name":"push-up","complexity":"EASY","description":"a classic bodyweight chest exercise",
            "movementType":"COMPOUND","targetBodyPart":"CHEST","isDeleted":false,"approved":false,"addedBy":"some-user",
            "_links":{"self":{"href":"http://localhost/api/exercises/1"}}}""";

    private static final Map<String, StubResponse> responses = new ConcurrentHashMap<>();
    private static final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
    private static final HttpServer exercisesService = startExercisesService();

    @Autowired
    private ExerciseRestClient exerciseRestClient;
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void registerExercisesService(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.ExercisesService[0].uri",
                () -> "http://localhost:" + exercisesService.getAddress().getPort());
    }

    @AfterAll
    static void stopExercisesService() {
        exercisesService.stop(0);
    }

    @BeforeEach
    void setup() {
        responses.clear();
        requests.clear();
    }

    @Test
    void testAddExerciseSendsApiFieldNames() throws Exception {
        respond("POST", "/api/exercises/add", 200, EXERCISE_JSON);

        ExerciseRestClient.ExerciseResponse added = exerciseRestClient.addExercise(new ExerciseAddBindingModel(
                "push-up",
                "a classic bodyweight chest exercise",
                TargetBodyPart.CHEST,
                "some-user",
                Complexity.EASY,
                MovementType.COMPOUND
        ));

        JsonNode body = objectMapper.readTree(requests.getFirst().body());
        assertEquals("push-up", body.get("name").asText());
        assertEquals("CHEST", body.get("bodyPart").asText());
        assertEquals("EASY", body.get("complexity").asText());
        assertEquals("COMPOUND", body.get("movement").asText());
        assertEquals("some-user", body.get("addedBy").asText());
        assertEquals(1L, added.id());
        assertFalse(added.approved());
    }

    @Test
    void testAddExerciseThrowsWhenExerciseAlreadyExists() {
        respond("POST", "/api/exercises/add", 409,
                "{\"exception\":\"ExerciseAlreadyExistsException\",\"message\":\"Exercise with name push-up already exists!\"}");

        ExerciseAlreadyExistsException exception = assertThrows(
                ExerciseAlreadyExistsException.class,
                () -> exerciseRestClient.addExercise(new ExerciseAddBindingModel(
                        "push-up", "a classic bodyweight chest exercise", TargetBodyPart.CHEST, "some-user", Complexity.EASY, MovementType.COMPOUND
                ))
        );

        assertEquals("Exercise with name push-up already exists!", exception.getMessage());
    }

    @Test
    void testGetExerciseEditBindingModelReadsExercise() {
        respond("GET", "/api/exercises/1", 200, EXERCISE_JSON);

        ExerciseEditBindingModel editModel = exerciseRestClient.getExerciseEditBindingModel(1L).getBody();

        assertNotNull(editModel);
        assertEquals(1L, editModel.id());
        assertEquals("push-up", editModel.name());
        assertEquals("a classic bodyweight chest exercise", editModel.description());
        assertFalse(editModel.approved());
    }

    @Test
    void testEditExerciseKeepsUnchangedFieldsAddsImagesAndApproves() throws Exception {
        respond("GET", "/api/exercises/1", 200, EXERCISE_JSON);
        respond("PATCH", "/api/exercises/1", 200, EXERCISE_JSON);
        respond("POST", "/api/exercises/1/images", 200, "{}");
        respond("PATCH", "/api/exercises/1/approve", 200, EXERCISE_JSON);

        exerciseRestClient.editExercise(new ExerciseEditBindingModel(
                1L,
                "wide push-up",
                "a push-up variation with hands wider than shoulders",
                "https://images.example/one.png\r\nhttps://images.example/two.png",
                true
        ));

        assertEquals(
                List.of("GET /api/exercises/1", "PATCH /api/exercises/1", "POST /api/exercises/1/images", "PATCH /api/exercises/1/approve"),
                requests.stream().map(request -> request.method() + " " + request.path()).toList()
        );

        JsonNode edit = objectMapper.readTree(requests.get(1).body());
        assertEquals("wide push-up", edit.get("name").asText());
        assertEquals("a push-up variation with hands wider than shoulders", edit.get("description").asText());
        assertEquals("CHEST", edit.get("bodyPart").asText());
        assertEquals("EASY", edit.get("complexity").asText());
        assertEquals("COMPOUND", edit.get("movement").asText());

        JsonNode images = objectMapper.readTree(requests.get(2).body());
        assertEquals("https://images.example/one.png", images.get("urls").get(0).asText());
        assertEquals("https://images.example/two.png", images.get("urls").get(1).asText());
    }

    @Test
    void testEditExerciseOnlyPatchesWhenNoImagesAndNotApproved() {
        respond("GET", "/api/exercises/1", 200, EXERCISE_JSON);
        respond("PATCH", "/api/exercises/1", 200, EXERCISE_JSON);

        exerciseRestClient.editExercise(new ExerciseEditBindingModel(
                1L, "push-up", "a classic bodyweight chest exercise", "", false
        ));

        assertEquals(
                List.of("GET /api/exercises/1", "PATCH /api/exercises/1"),
                requests.stream().map(request -> request.method() + " " + request.path()).toList()
        );
    }

    @Test
    void testApproveExerciseThrowsWhenExerciseDoesNotExist() {
        respond("PATCH", "/api/exercises/0/approve", 404,
                "{\"exception\":\"ExerciseNotFoundException\",\"message\":\"Exercise not found\"}");

        assertThrows(ExerciseNotFoundException.class, () -> exerciseRestClient.approveExercise(0L));
    }

    @Test
    void testGetExercisesForReviewPageReadsPagedModel() {
        respond("GET", "/api/exercises/for-review", 200, """
                {"_embedded":{"exerciseModelList":[%s]},
                "_links":{"self":{"href":"http://localhost/api/exercises/for-review?page=1&size=1"}},
                "page":{"size":1,"totalElements":3,"totalPages":3,"number":1}}""".formatted(EXERCISE_JSON));

        Page<ExerciseForReviewViewModel> page = exerciseRestClient.getExercisesForReviewPage(1, 1, "desc");

        assertEquals("page=1&size=1&orderBy=desc", requests.getFirst().query());
        assertEquals(1, page.getNumber());
        assertEquals(1, page.getSize());
        assertEquals(3, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertEquals(
                List.of(new ExerciseForReviewViewModel(1L, "push-up", "a classic bodyweight chest exercise", Complexity.EASY, MovementType.COMPOUND, "some-user")),
                page.getContent()
        );
    }

    @Test
    void testGetExercisesForReviewPageReadsEmptyPagedModel() {
        respond("GET", "/api/exercises/for-review", 200, """
                {"_links":{"self":{"href":"http://localhost/api/exercises/for-review?page=0&size=10"}},
                "page":{"size":10,"totalElements":0,"totalPages":0,"number":0}}""");

        Page<ExerciseForReviewViewModel> page = exerciseRestClient.getExercisesForReviewPage(0, 10, "asc");

        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void testGetExercisesForReviewCountReadsCount() {
        respond("GET", "/api/exercises/for-review/count", 200, "4");

        assertEquals(4L, exerciseRestClient.getExercisesForReviewCount().getBody());
    }

    private static void respond(String method, String path, int status, String body) {
        responses.put(method + " " + path, new StubResponse(status, body));
    }

    private static HttpServer startExercisesService() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/", exchange -> {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                requests.add(new RecordedRequest(method, path, exchange.getRequestURI().getQuery(), requestBody));

                StubResponse response = responses.getOrDefault(method + " " + path, new StubResponse(404, "{}"));
                byte[] body = response.body().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/hal+json");
                exchange.sendResponseHeaders(response.status(), body.length);
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

    private record StubResponse(int status, String body) {
    }

    private record RecordedRequest(String method, String path, String query, String body) {
    }

}

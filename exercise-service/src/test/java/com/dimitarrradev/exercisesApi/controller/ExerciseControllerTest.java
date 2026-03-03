package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
import com.dimitarrradev.exercisesApi.controller.binding.ImageUrlsAddModel;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.dao.ImageUrlRepository;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
import com.dimitarrradev.exercisesApi.exercise.model.ImageUrl;
import com.dimitarrradev.exercisesApi.exercise.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExerciseControllerTest {

    private static MockHttpServletRequest request;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private ImageUrlRepository imageUrlRepository;
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sql.script.create.exercise}")
    private String createExercise;
    @Value("${sql.script.delete.exercise}")
    private String deleteExercise;
    @Value("${sql.script.create.image}")
    private String createImage;
    @Value("${sql.script.delete.image}")
    private String deleteImage;

    @BeforeEach
    void setup() {
        jdbc.execute(String.format(createExercise, "test-exercise-1", "test-exercise-description", "ABS", "EASY", "ISOLATION"));
        jdbc.execute(String.format(createImage, "https://someuriwithimages.link", 1));
    }

    @AfterEach
    void teardown() {
        jdbc.execute(deleteImage);
        jdbc.execute("ALTER TABLE image_urls ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute(deleteExercise);
        jdbc.execute("ALTER TABLE exercises ALTER COLUMN ID RESTART WITH 1");
    }

    @Test
    void testGetExerciseReturnsExistingExercise() throws Exception {
        Optional<Exercise> byId = exerciseRepository.findById(1L);

        assertTrue(byId.isPresent());

        Exercise exercise = byId.get();

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}", exercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name", is(exercise.getName())))
                .andExpect(jsonPath("$.complexity", is(exercise.getComplexity().toString())))
                .andExpect(jsonPath("$.movementType", is(exercise.getMovementType().toString())))
                .andExpect(jsonPath("$.description", is(exercise.getDescription())))
                .andExpect(jsonPath("$._links", aMapWithSize(4)))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")))
                .andExpect(jsonPath("$._links", hasKey("images")));
    }

    @Test
    void testGetExerciseRespondsWithStatusNotFoundWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> byId = exerciseRepository.findById(0L);

        assertTrue(byId.isEmpty());

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}", 0L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found")));
    }

    @Test
    void addExerciseRespondsWithStatusBadRequestWhenRequestBodyIsNotValid() throws Exception {
        ExerciseAddModel addModel = new ExerciseAddModel("", "", null, null, null);

        mockMvc.perform(post("https://localhost:8082/api/exercises/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(5)));
    }

    @Test
    void addExerciseRespondsWithStatusConflictWhenExerciseNameExists() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent());

        Exercise exercise = optionalExercise.get();

        ExerciseAddModel addModel = new ExerciseAddModel(
                exercise.getName(),
                exercise.getDescription(),
                exercise.getTargetBodyPart(),
                exercise.getComplexity(),
                exercise.getMovementType()
        );

        mockMvc.perform(post("https://localhost:8082/api/exercises/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addModel)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseAlreadyExistsException")))
                .andExpect(jsonPath("$.message", is("Exercise with name " + exercise.getName() + " already exists!")));
    }

    @Test
    void addExerciseCreatesNewExerciseWhenRequestBodyIsValid() throws Exception {
        ExerciseAddModel addModel = new ExerciseAddModel(
                "new-test-exercise",
                "this is a valid description",
                TargetBodyPart.ABDUCTORS,
                Complexity.HARD,
                MovementType.ISOLATION
        );

        mockMvc.perform(post("https://localhost:8082/api/exercises/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addModel)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(addModel.name())))
                .andExpect(jsonPath("$.complexity", is(addModel.complexity().toString())))
                .andExpect(jsonPath("$.description", is(addModel.description())))
                .andExpect(jsonPath("$.movementType", is(addModel.movement().toString())))
                .andExpect(jsonPath("$.targetBodyPart", is(addModel.bodyPart().toString())))
                .andExpect(jsonPath("$.targetBodyPart", is(addModel.bodyPart().toString())))
                .andExpect(jsonPath("$._links", aMapWithSize(4)))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")))
                .andExpect(jsonPath("$._links", hasKey("images")));
    }

    @Test
    void testEditExerciseRespondsWithStatusBadRequestWhenModelIsNotValid() throws Exception {
        ExerciseEditModel editModel = new ExerciseEditModel(null, null, null, null, null);

        mockMvc.perform(patch("https://localhost:8082/api/exercises/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editModel)))
                .andExpect(jsonPath("$.exception", is("InvalidRequestBodyException")))
                .andExpect(jsonPath("$.message", is("Invalid request body fields!")))
                .andExpect(jsonPath("$.fields", hasSize(5)));
    }

    @Test
    void testEditExerciseEditsExistingExerciseWhenRequestBodyIsValid() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent());

        Exercise exercise = optionalExercise.get();

        ExerciseEditModel editModel = new ExerciseEditModel(
                "new-test-exercise-name",
                "this is a valid description",
                TargetBodyPart.ABDUCTORS,
                Complexity.HARD,
                MovementType.ISOLATION
        );

        mockMvc.perform(patch("https://localhost:8082/api/exercises/{id}", exercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editModel)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(editModel.name())))
                .andExpect(jsonPath("$.complexity", is(editModel.complexity().toString())))
                .andExpect(jsonPath("$.description", is(editModel.description())))
                .andExpect(jsonPath("$.movementType", is(editModel.movement().toString())))
                .andExpect(jsonPath("$.targetBodyPart", is(editModel.bodyPart().toString())))
                .andExpect(jsonPath("$.targetBodyPart", is(editModel.bodyPart().toString())))
                .andExpect(jsonPath("$._links", aMapWithSize(4)))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")))
                .andExpect(jsonPath("$._links", hasKey("images")));
    }

    @Test
    void testEditExerciseRespondsWithStatusNotFoundWhenExerciseNotFoundAndRequestBodyIsValid() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(0L);

        assertFalse(optionalExercise.isPresent());

        ExerciseEditModel editModel = new ExerciseEditModel(
                "new-test-exercise-name",
                "this is a valid description",
                TargetBodyPart.ABDUCTORS,
                Complexity.HARD,
                MovementType.ISOLATION
        );

        mockMvc.perform(patch("https://localhost:8082/api/exercises/{id}", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editModel)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found")));

    }

    @Test
    void testSearchReturnsCorrectPagedModelOfExerciseModel() throws Exception {
        jdbc.execute(String.format(createExercise, "test-exercise-2", "test-exercise-description", "ABS", "EASY", "ISOLATION"));
        jdbc.execute(String.format(createExercise, "test-exercise-3", "test-exercise-description", "ABS", "EASY", "ISOLATION"));
        jdbc.execute(String.format(createExercise, "test-exercise-4", "test-exercise-description", "ABS", "EASY", "ISOLATION"));
        jdbc.execute(String.format(createExercise, "test-exercise-5", "test-exercise-description", "ABS", "EASY", "ISOLATION"));

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(0, 2, sort);

        Page<Exercise> allByIsDeletedFalse = exerciseRepository.findAllByIsDeletedFalse(pageable);

        assertEquals(3, allByIsDeletedFalse.getTotalPages());
        assertEquals(5, allByIsDeletedFalse.getTotalElements());
        assertEquals(2, allByIsDeletedFalse.getSize());
        assertEquals(0, allByIsDeletedFalse.getNumber());

        mockMvc.perform(get("https://localhost:8082/api/exercises/search").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.exerciseModelList", hasSize(2)))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(5)))
                .andExpect(jsonPath("$.page.totalPages", is(3)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$._links", aMapWithSize(4)))
                .andExpect(jsonPath("$._links", hasKey("first")))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("next")))
                .andExpect(jsonPath("$._links", hasKey("last")));
    }

    @Test
    void testDeleteExerciseRespondsWithStatusBadRequestWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> empty = exerciseRepository.findById(0L);

        assertTrue(empty.isEmpty(), "Exercise should not exist");

        mockMvc.perform(delete("https://localhost:8082/api/exercises/{id}", 0L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found!")));
    }

    @Test
    void testDeleteExerciseSetsDeletedTrueAndReturnsValidModel() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent(), "Exercise should be present");

        Exercise exercise = optionalExercise.get();

        mockMvc.perform(delete("https://localhost:8082/api/exercises/{id}", exercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id", is(exercise.getId().intValue())))
                .andExpect(jsonPath("$.name", is(exercise.getName())))
                .andExpect(jsonPath("$.complexity", is(exercise.getComplexity().toString())))
                .andExpect(jsonPath("$.description", is(exercise.getDescription())))
                .andExpect(jsonPath("$.movementType", is(exercise.getMovementType().toString())))
                .andExpect(jsonPath("$.targetBodyPart", is(exercise.getTargetBodyPart().toString())))
                .andExpect(jsonPath("$.isDeleted", is(Boolean.TRUE)))
                .andExpect(jsonPath("$._links", aMapWithSize(3)))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("images")));

        Optional<Exercise> optionalExerciseAfter = exerciseRepository.findById(1L);

        assertTrue(optionalExerciseAfter.isPresent(), "Exercise should be present");

        Exercise deleted = optionalExercise.get();

        assertTrue(deleted.getIsDeleted());
    }

    @Test
    void testGetImagesRespondsWithStatusNotFoundWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> empty = exerciseRepository.findById(0L);

        assertTrue(empty.isEmpty(), "Exercise should not exist");

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}/images", 0L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found!")));
    }

    @Test
    void testGetImagesReturnsCorrectCollectionOfImageUrlModel() throws Exception {
        jdbc.execute(String.format(createImage, "some-image-url", 1));
        jdbc.execute(String.format(createImage, "some-other-image-url", 1));

        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent(), "Exercise should be present");

        Exercise exercise = optionalExercise.get();

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}/images", exercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.imageUrlModelList", hasSize(3)));
    }

    @Test
    void testGetImageRespondsWithStatusNotFoundWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> empty = exerciseRepository.findById(0L);

        assertTrue(empty.isEmpty(), "Exercise should not exist");

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}/images/{imageId}", 0L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ImageNotFoundException")))
                .andExpect(jsonPath("$.message", is("Image or exercise does not exist!")));
    }

    @Test
    void testAddImagesRespondsWithStatusNotFoundWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> empty = exerciseRepository.findById(0L);

        assertTrue(empty.isEmpty(), "Exercise should not exist");

        mockMvc.perform(post("https://localhost:8082/api/exercises/{id}/images", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ImageUrlsAddModel(Collections.emptyList()))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found!")));
    }

    @Test
    void testAddImagesAddsImagesAndReturnsCorrectCollectionModel() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent(), "Exercise should be present");

        Exercise exercise = optionalExercise.get();

        assertEquals(1, imageUrlRepository.count());

        mockMvc.perform(post("https://localhost:8082/api/exercises/{id}/images", exercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ImageUrlsAddModel(List.of("some-url", "another-url")))))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.imageUrlModelList", hasSize(2)));

        assertEquals(3, imageUrlRepository.count());
    }

    @Test
    void testDeleteImageRespondsWithStatusNotFoundWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> empty = exerciseRepository.findById(0L);

        assertTrue(empty.isEmpty(), "Exercise should not exist");

        mockMvc.perform(delete("https://localhost:8082/api/exercises/{id}/images/{image_id}", 0L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ImageNotFoundException")))
                .andExpect(jsonPath("$.message", is("Image or exercise does not exist!")));
    }

    @Test
    void testDeleteImageRemovesImageAndReturnsModel() throws Exception {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(1L);

        assertTrue(optionalExercise.isPresent(), "Exercise should be present");

        Optional<ImageUrl> optionalImageUrl = imageUrlRepository.findByIdAndExercise_id(1L, 1L);

        assertTrue(optionalImageUrl.isPresent(), "Image should be present");

        ImageUrl imageUrl = optionalImageUrl.get();

        mockMvc.perform(delete("https://localhost:8082/api/exercises/{id}/images/{image_id}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id", is(imageUrl.getId().intValue())))
                .andExpect(jsonPath("$.url", is(imageUrl.getUrl())))
                .andExpect(jsonPath("$.exerciseId", is(imageUrl.getExercise().getId().intValue())))
                .andExpect(jsonPath("$.isDeleted", is(Boolean.TRUE)))
                .andExpect(jsonPath("$._links", aMapWithSize(1)))
                .andExpect(jsonPath("$._links", hasKey("exercise")));

        assertEquals(0, imageUrlRepository.count());
    }


}

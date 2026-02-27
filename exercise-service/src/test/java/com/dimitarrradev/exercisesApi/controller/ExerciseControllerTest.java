package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.controller.binding.ExerciseAddModel;
import com.dimitarrradev.exercisesApi.controller.binding.ExerciseEditModel;
import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.exercise.model.Exercise;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private ExerciseService exerciseService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sql.script.create.exercise}")
    private String createExercise;
    @Value("${sql.script.delete.exercise}")
    private String deleteExercise;

    @BeforeEach
    void setup() {
        jdbc.execute(createExercise);
    }

    @AfterEach
    void teardown() {
        jdbc.execute(deleteExercise);
        jdbc.execute("ALTER TABLE exercises ALTER COLUMN ID RESTART WITH 1");
    }

    @Test
    void testGetExerciseReturnsExistingExercise() throws Exception {
        Optional<Exercise> byName = exerciseRepository.findByName("test-exercise");

        assertTrue(byName.isPresent());

        Exercise exercise = byName.get();

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}", exercise.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name", is(exercise.getName())))
                .andExpect(jsonPath("$.complexity", is(exercise.getComplexity().toString())))
                .andExpect(jsonPath("$.movementType", is(exercise.getMovementType().toString())))
                .andExpect(jsonPath("$.description", is(exercise.getDescription())))
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")));
    }

    @Test
    void testGetExerciseThrowsWhenExerciseDoesNotExist() throws Exception {
        Optional<Exercise> byId = exerciseRepository.findById(0L);

        assertTrue(byId.isEmpty());

        mockMvc.perform(get("https://localhost:8082/api/exercises/{id}", 0L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", is("ExerciseNotFoundException")))
                .andExpect(jsonPath("$.message", is("Exercise not found")));
    }

    @Test
    void addExerciseReturnsBadRequestWhenRequestBodyIsNotValid() throws Exception {
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
    void addExerciseReturnsConflictWhenExerciseNameExists() throws Exception {
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
                .andExpect(jsonPath("$.message", is("Exercise with name test-exercise  already exists")));
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
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")))
                .andExpect(jsonPath("$._links", hasKey("images")));
    }

    @Test
    void testEditExerciseReturnsBadRequestWhenModelIsNotValid() throws Exception {
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
                .andExpect(jsonPath("$._links", hasKey("self")))
                .andExpect(jsonPath("$._links", hasKey("update")))
                .andExpect(jsonPath("$._links", hasKey("delete")))
                .andExpect(jsonPath("$._links", hasKey("images")));
    }

    @Test
    void testEditExerciseReturnsNotFoundWhenExerciseNotFoundAndRequestBodyIsValid() throws Exception {
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


//
////    @Test
////    @Sql(scripts = {"/dbContent/users.sql", "/dbContent/exercises.sql"})
////    void testSecurity() throws Exception {
////
////        ResponseEntity<String> forEntity = restTemplate
////                .withBasicAuth("test-user", "password")
////                .getForEntity("/exercises/1", String.class, 1);
////
////        assertThat(forEntity.getStatusCode())
////                .isEqualTo(HttpStatus.OK);
////    }
//
//    @Test
////    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
//    @DirtiesContext
//    void testPostAddExerciseShouldCreateExerciseAndSaveItInRepository() throws Exception {
//        String name = "test-exercise";
//        String description = "test-exercise-description";
//
//        mockMvc.perform(post("/api/exercises/add")
//                        .param("name", name)
//                        .param("description", description)
//                        .param("bodyPart", "abs")
//                        .param("addedBy", "user")
//                        .param("complexity", "easy")
//                        .param("movement", "isolation")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//
//
//        mockMvc.perform(get("/api/exercises/{id}", 1)
////                        .with(user("test-user").roles("ADMINISTRATOR")))
//                ).andExpect(status().isOk())
//                .andExpect(content().contentType("application/hal+json"))
//                .andExpect(jsonPath("$.name").value(name))
//                .andExpect(jsonPath("$.complexity").value("EASY"))
//                .andExpect(jsonPath("$.movementType").value("ISOLATION"))
//                .andExpect(jsonPath("$.description").value(description));
//    }
//
//    @Test
//    @Sql(scripts = "/dbContent/exercises.sql")
////    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
//    @DirtiesContext
//    void testPostEditExerciseShouldEditExerciseAndSaveItInRepository() throws Exception {
//        mockMvc.perform(patch("/api/exercises/edit/{id}", 1)
//                        .param("name", "new name")
//                        .param("description", "new description")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @Sql(scripts = "/dbContent/exercises.sql")
////    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
//    @DirtiesContext
//    void testGetExercisesShouldReturnCorrectResponse() throws Exception {
//        int page = 0;
//        int size = 5;
//        String orderBy = "asc";
//
//        mockMvc.perform(get("/api/exercises/")
//                .param("page", String.valueOf(page))
//                .param("size", String.valueOf(size))
//                .param("orderBy", orderBy)
//        ).andExpect(status().isOk())
//                .andExpect(content().contentType("application/hal+json"))
//                .andExpect(jsonPath("$._embedded.exerciseModelList").isArray())
//                .andExpect(jsonPath("$._embedded.exerciseModelList").isNotEmpty());
//
//    }


}

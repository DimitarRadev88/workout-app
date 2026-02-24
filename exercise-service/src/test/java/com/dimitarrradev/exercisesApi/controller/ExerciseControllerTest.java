package com.dimitarrradev.exercisesApi.controller;

import com.dimitarrradev.exercisesApi.exercise.dao.ExerciseRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application-test.properties")
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

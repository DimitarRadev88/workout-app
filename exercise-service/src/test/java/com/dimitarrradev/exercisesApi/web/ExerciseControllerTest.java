package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.exercise.enums.Complexity;
import com.dimitarrradev.exercisesApi.exercise.enums.MovementType;
import com.dimitarrradev.exercisesApi.exercise.enums.TargetBodyPart;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseAddBindingModel;
import com.dimitarrradev.exercisesApi.web.binding.ExerciseEditBindingModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ExerciseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = "/dbContent/exercises.sql")
    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testGetExerciseShouldReturnExercise() throws Exception {

        mockMvc.perform(get("/exercises/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("test-exercise1"))
                .andExpect(jsonPath("$.complexity").value("MEDIUM"))
                .andExpect(jsonPath("$.movementType").value("COMPOUND"))
                .andExpect(jsonPath("$.description").value("test-exercise-description1"))
                .andExpect(jsonPath("$.imageUrls").isArray());

    }

//    @Test
//    @Sql(scripts = {"/dbContent/users.sql", "/dbContent/exercises.sql"})
//    void testSecurity() throws Exception {
//
//        ResponseEntity<String> forEntity = restTemplate
//                .withBasicAuth("test-user", "password")
//                .getForEntity("/exercises/1", String.class, 1);
//
//        assertThat(forEntity.getStatusCode())
//                .isEqualTo(HttpStatus.OK);
//    }

    @Test
    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testPostAddExerciseShouldCreateExerciseAndSaveItInRepository() throws Exception {
        ExerciseAddBindingModel bindingModel = new ExerciseAddBindingModel("test-exercise",
                "test-exercise-description",
                TargetBodyPart.ABS,
                "user",
                Complexity.EASY,
                MovementType.ISOLATION);

        mockMvc.perform(post("/exercises/add")
                        .content(objectMapper.writeValueAsBytes(bindingModel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/exercises/1"));

        mockMvc.perform(get("/exercises/{id}", 1)
                        .with(user("test-user").roles("ADMINISTRATOR")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(bindingModel.exerciseName()))
                .andExpect(jsonPath("$.complexity").value(bindingModel.complexity().toString()))
                .andExpect(jsonPath("$.movementType").value(bindingModel.movementType().toString()))
                .andExpect(jsonPath("$.description").value(bindingModel.description()))
                .andExpect(jsonPath("$.imageUrls").isArray());
    }

    @Test
    @Sql(scripts = "/dbContent/exercises.sql")
    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testPostEditExerciseShouldEditExerciseAndSaveItInRepository() throws Exception {
        ExerciseEditBindingModel bindingModel = new ExerciseEditBindingModel(1L, "edited-exercise",
                "edited-exercise-description",
                null,
                Boolean.TRUE);

        mockMvc.perform(patch("/exercises/edit/{id}", 1)
                        .content(objectMapper.writeValueAsBytes(bindingModel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "/dbContent/exercises.sql")
    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testGetExercisesForReviewShouldReturnCorrectResponse() throws Exception {

        String linkHeader = "<http://localhost:8082/exercises/for-review?page=%d&size=%d&orderBy=%s>;, rel=\"%s\"";
        String link = "Link";

        int page = 0;
        int size = 5;
        String orderBy = "asc";

        mockMvc.perform(get("/exercises/for-review")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("orderBy", orderBy)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(header().stringValues(link,
                        String.format(linkHeader, page, size, orderBy, "self"),
                        String.format(linkHeader, 0, size, orderBy, "first"),
                        String.format(linkHeader, 0, size, orderBy, "prev"),
                        String.format(linkHeader, 0, size, orderBy, "next"),
                        String.format(linkHeader, 0, size, orderBy, "last")
                ));

    }


}

package com.dimitarrradev.exercisesApi.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

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
//    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testGetExerciseShouldReturnExercise() throws Exception {

        mockMvc.perform(get("/api/exercises/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name").value("test-exercise1"))
                .andExpect(jsonPath("$.complexity").value("MEDIUM"))
                .andExpect(jsonPath("$.movementType").value("COMPOUND"))
                .andExpect(jsonPath("$.description").value("test-exercise-description1"));
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
//    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testPostAddExerciseShouldCreateExerciseAndSaveItInRepository() throws Exception {
        String name = "test-exercise";
        String description = "test-exercise-description";

        mockMvc.perform(post("/api/exercises/add")
                        .param("name", name)
                        .param("description", description)
                        .param("bodyPart", "abs")
                        .param("addedBy", "user")
                        .param("complexity", "easy")
                        .param("movement", "isolation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/exercises/{id}", 1)
//                        .with(user("test-user").roles("ADMINISTRATOR")))
                ).andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.complexity").value("EASY"))
                .andExpect(jsonPath("$.movementType").value("ISOLATION"))
                .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @Sql(scripts = "/dbContent/exercises.sql")
//    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testPostEditExerciseShouldEditExerciseAndSaveItInRepository() throws Exception {
        mockMvc.perform(patch("/api/exercises/edit/{id}", 1)
                        .param("name", "new name")
                        .param("description", "new description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "/dbContent/exercises.sql")
//    @WithMockUser(username = "test-user", roles = {"ADMINISTRATOR"})
    @DirtiesContext
    void testGetExercisesShouldReturnCorrectResponse() throws Exception {
        int page = 0;
        int size = 5;
        String orderBy = "asc";

        mockMvc.perform(get("/api/exercises/")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("orderBy", orderBy)
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.exerciseModelList").isArray())
                .andExpect(jsonPath("$._embedded.exerciseModelList").isNotEmpty());

    }


}

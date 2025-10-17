package com.dimitarrradev.exercisesApi.web;

import com.dimitarrradev.exercisesApi.image.dto.ImageUrlViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ExerciseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @Sql(scripts = {"/dbContent/exercises.sql"})
    void testGetExerciseShouldReturnExercise() throws Exception {

        mockMvc.perform(get("/exercises/{id}", 1)
                        .with(user("test-user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test-exercise1"))
                .andExpect(jsonPath("$.complexity").value("Medium"))
                .andExpect(jsonPath("$.movementType").value("Compound"))
                .andExpect(jsonPath("$.description").value("test-exercise-description1"))
                .andExpect(jsonPath("$.imageUrls").isArray());

    }
}

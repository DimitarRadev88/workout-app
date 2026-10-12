package com.dimitarrradev.workoutScheduler.web;

import com.dimitarrradev.workoutScheduler.exercise.service.ExerciseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExerciseService exerciseService;

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotApproveExercise() throws Exception {
        mockMvc.perform(patch("/exercises/approve/{id}", 1L).with(csrf()))
                .andExpect(status().isForbidden());

        verify(exerciseService, never()).approveExercise(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotDeleteExercise() throws Exception {
        mockMvc.perform(delete("/exercises/delete/{id}", 1L).with(csrf()))
                .andExpect(status().isForbidden());

        verify(exerciseService, never()).deleteExercise(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotOpenExercisesForReview() throws Exception {
        mockMvc.perform(get("/exercises/for-review"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotOpenExerciseEditPage() throws Exception {
        mockMvc.perform(get("/exercises/edit/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void testAdminCanApproveExercise() throws Exception {
        mockMvc.perform(patch("/exercises/approve/{id}", 1L).with(csrf()))
                .andExpect(redirectedUrl("/exercises/for-review"));

        verify(exerciseService).approveExercise(1L);
    }

}

package com.scheduleService.schedule.controller;

import com.scheduleService.schedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ScheduleService scheduleService;

    @Test
    void testGetWithDateReturnsScheduleForThatDate() throws Exception {
        mockMvc.perform(get("/api/schedules/{userId}", 1L).param("date", "2026-10-12"))
                .andExpect(status().isOk());

        verify(scheduleService).getSchedule(1L, LocalDate.of(2026, 10, 12));
        verify(scheduleService, never()).getAll(any());
    }

    @Test
    void testGetWithoutDateReturnsAllSchedules() throws Exception {
        mockMvc.perform(get("/api/schedules/{userId}", 1L))
                .andExpect(status().isOk());

        verify(scheduleService).getAll(1L);
        verify(scheduleService, never()).getSchedule(any(), any());
    }

}

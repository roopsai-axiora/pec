package com.axiora.pec.goal.controller;

import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.goal.dto.GoalRequest;
import com.axiora.pec.goal.dto.GoalResponse;
import com.axiora.pec.goal.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(goalController)
                .setValidator(validator)
                .build();
    }

    private GoalResponse buildGoalResponse() {
        return new GoalResponse(
                1L,
                "Improve Code Quality",
                "Increase test coverage",
                new BigDecimal("30.00"),
                GoalStatus.ACTIVE,
                "2026-Q1",
                "Roop Sai",
                "roop@axiora.com",
                Instant.now()
        );
    }

    private GoalRequest buildGoalRequest() {
        return new GoalRequest(
                "Improve Code Quality",
                "Increase test coverage",
                new BigDecimal("30.00"),
                "2026-Q1",
                1L
        );
    }

    @Test
    void shouldGetGoalById() throws Exception {
        when(goalService.getById(1L))
                .thenReturn(buildGoalResponse());

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Improve Code Quality"));
    }

    @Test
    void shouldGetGoalsByUser() throws Exception {
        when(goalService.getByUser(1L))
                .thenReturn(List.of(buildGoalResponse()));

        mockMvc.perform(get("/api/goals/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("Improve Code Quality"));
    }

    @Test
    void shouldUpdateGoal() throws Exception {
        when(goalService.update(eq(1L), any()))
                .thenReturn(buildGoalResponse());

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(buildGoalRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldDeleteGoal() throws Exception {
        doNothing().when(goalService).delete(1L);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestForInvalidGoal()
            throws Exception {
        GoalRequest invalidRequest = new GoalRequest(
                "",
                null,
                null,
                "",
                null
        );

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
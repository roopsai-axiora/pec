package com.axiora.pec.kpi.controller;

import com.axiora.pec.kpi.dto.KpiRequest;
import com.axiora.pec.kpi.dto.KpiResponse;
import com.axiora.pec.kpi.service.KpiService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class KpiControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Mock
    private KpiService kpiService;

    @InjectMocks
    private KpiController kpiController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(kpiController)
                .setValidator(validator)
                .build();
    }

    private KpiResponse buildKpiResponse() {
        return new KpiResponse(
                1L,
                1L,
                "Improve Code Quality",
                new BigDecimal("100.00"),
                new BigDecimal("85.00"),
                new BigDecimal("85.00"),
                "2026-Q1",
                "Good progress",
                "Roop Sai",
                Instant.now()
        );
    }


    @Test
    void shouldGetKpiById() throws Exception {
        when(kpiService.getById(1L))
                .thenReturn(buildKpiResponse());

        mockMvc.perform(get("/api/kpis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalTitle")
                        .value("Improve Code Quality"));
    }

    @Test
    void shouldGetKpisByGoal() throws Exception {
        when(kpiService.getByGoal(1L))
                .thenReturn(List.of(buildKpiResponse()));

        mockMvc.perform(get("/api/kpis/goal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetKpisByUser() throws Exception {
        when(kpiService.getByUser(1L))
                .thenReturn(List.of(buildKpiResponse()));

        mockMvc.perform(get("/api/kpis/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetKpisByPeriod() throws Exception {
        when(kpiService.getByPeriod("2026-Q1"))
                .thenReturn(List.of(buildKpiResponse()));

        mockMvc.perform(get("/api/kpis/period/2026-Q1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldReturnBadRequestForInvalidKpi()
            throws Exception {
        KpiRequest invalidRequest = new KpiRequest(
                null,
                null,
                null,
                "",
                null
        );

        mockMvc.perform(post("/api/kpis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
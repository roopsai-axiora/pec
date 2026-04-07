package com.axiora.pec.evaluation.controller;

import com.axiora.pec.evaluation.domain.EvaluationStatus;
import com.axiora.pec.evaluation.dto.EvaluationRequest;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import com.axiora.pec.evaluation.service.EvaluationScorecardPdfService;
import com.axiora.pec.evaluation.service.EvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EvaluationControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EvaluationService evaluationService;

    @Mock
    private EvaluationScorecardPdfService scorecardPdfService;

    @InjectMocks
    private EvaluationController evaluationController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(evaluationController)
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldEvaluateUser() throws Exception {
        when(evaluationService.evaluate(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new EvaluationRequest(1L, "2026-Q1")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.period").value("2026-Q1"));
    }

    @Test
    void shouldGetEvaluationById() throws Exception {
        when(evaluationService.getById(1L))
                .thenReturn(buildResponse());

        mockMvc.perform(get("/api/evaluations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldGetEvaluationsByUser() throws Exception {
        when(evaluationService.getByUser(1L))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/evaluations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].finalScore").value(95.0));
    }

    @Test
    void shouldDownloadScorecardPdf() throws Exception {
        byte[] pdf = "pdf".getBytes();
        when(evaluationService.getById(1L))
                .thenReturn(buildResponse());
        when(scorecardPdfService.generate(any()))
                .thenReturn(pdf);

        mockMvc.perform(get("/api/evaluations/1/scorecard"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"evaluation-scorecard-1.pdf\""
                ))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }

    private EvaluationResponse buildResponse() {
        return new EvaluationResponse(
                1L,
                1L,
                "Roop Sai",
                "2026-Q1",
                new BigDecimal("95.00"),
                EvaluationStatus.COMPLETED,
                false,
                List.of(new GoalScoreDetail(
                        1L,
                        "Improve Code Quality",
                        new BigDecimal("100.00"),
                        new BigDecimal("100.00"),
                        new BigDecimal("95.00"),
                        new BigDecimal("95.00"),
                        new BigDecimal("95.00"),
                        false,
                        "No rule matched",
                        "N/A"
                )),
                Instant.now()
        );
    }
}

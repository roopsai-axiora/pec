package com.axiora.pec.rule.controller;

import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import com.axiora.pec.rule.dto.RuleRequest;
import com.axiora.pec.rule.dto.RuleResponse;
import com.axiora.pec.rule.service.RuleService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private RuleController ruleController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(ruleController)
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldCreateRule() throws Exception {
        when(ruleService.create(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("High Achiever"));
    }

    @Test
    void shouldGetAllRules() throws Exception {
        when(ruleService.getAll())
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operator").value("GT"));
    }

    @Test
    void shouldGetActiveRules() throws Exception {
        when(ruleService.getActiveRules())
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/rules/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldGetRuleById() throws Exception {
        when(ruleService.getById(1L))
                .thenReturn(buildResponse());

        mockMvc.perform(get("/api/rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldUpdateRule() throws Exception {
        when(ruleService.update(eq(1L), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(put("/api/rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("ADD"));
    }

    @Test
    void shouldActivateRule() throws Exception {
        doNothing().when(ruleService).activate(1L);

        mockMvc.perform(patch("/api/rules/1/activate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeactivateRule() throws Exception {
        doNothing().when(ruleService).deactivate(1L);

        mockMvc.perform(patch("/api/rules/1/deactivate"))
                .andExpect(status().isNoContent());
    }

    private RuleRequest buildRequest() {
        return new RuleRequest(
                "High Achiever",
                "High achievement rule",
                RuleOperator.GT,
                new BigDecimal("90.00"),
                null,
                RuleAction.ADD,
                new BigDecimal("10.00"),
                1
        );
    }

    private RuleResponse buildResponse() {
        return RuleResponse.builder()
                .id(1L)
                .name("High Achiever")
                .description("High achievement rule")
                .operator(RuleOperator.GT)
                .thresholdValue(new BigDecimal("90.00"))
                .action(RuleAction.ADD)
                .actionValue(new BigDecimal("10.00"))
                .priority(1)
                .active(true)
                .createdAt(Instant.now())
                .build();
    }
}

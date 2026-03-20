package com.axiora.pec.rule.service;

import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import com.axiora.pec.rule.dto.RuleRequest;
import com.axiora.pec.rule.dto.RuleResponse;
import com.axiora.pec.rule.engine.RuleValidator;
import com.axiora.pec.rule.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RuleValidator ruleValidator;

    @InjectMocks
    private RuleService ruleService;

    private Rule testRule;
    private RuleRequest ruleRequest;

    @BeforeEach
    void setUp() {
        testRule = Rule.builder()
                .id(1L)
                .name("High Achiever")
                .description("Achievement > 90%")
                .operator(RuleOperator.GT)
                .thresholdValue(new BigDecimal("90.00"))
                .action(RuleAction.ADD)
                .actionValue(new BigDecimal("10.00"))
                .priority(1)
                .active(true)
                .build();

        ruleRequest = new RuleRequest(
                "High Achiever",
                "Achievement > 90%",
                RuleOperator.GT,
                new BigDecimal("90.00"),
                null,
                RuleAction.ADD,
                new BigDecimal("10.00"),
                1
        );
    }

    @Test
    void shouldCreateRuleSuccessfully() {
        when(ruleRepository.existsByPriority(anyInt()))
                .thenReturn(false);
        when(ruleRepository.save(any()))
                .thenReturn(testRule);

        RuleResponse response =
                ruleService.create(ruleRequest);

        assertNotNull(response);
        assertEquals("High Achiever", response.getName());
        assertEquals(RuleOperator.GT,
                response.getOperator());
        verify(ruleRepository, times(1)).save(any());
        verify(ruleValidator, times(1)).validate(any());
    }

    @Test
    void shouldThrowWhenDuplicatePriority() {
        when(ruleRepository.existsByPriority(1))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> ruleService.create(ruleRequest));

        verify(ruleRepository, never()).save(any());
    }

    @Test
    void shouldGetAllRules() {
        when(ruleRepository.findAllByOrderByPriorityAsc())
                .thenReturn(List.of(testRule));

        List<RuleResponse> rules = ruleService.getAll();

        assertNotNull(rules);
        assertEquals(1, rules.size());
        assertEquals("High Achiever",
                rules.get(0).getName());
    }

    @Test
    void shouldGetActiveRules() {
        when(ruleRepository
                .findByActiveTrueOrderByPriorityAsc())
                .thenReturn(List.of(testRule));

        List<RuleResponse> rules =
                ruleService.getActiveRules();

        assertNotNull(rules);
        assertEquals(1, rules.size());
    }

    @Test
    void shouldGetRuleById() {
        when(ruleRepository.findById(1L))
                .thenReturn(Optional.of(testRule));

        RuleResponse response = ruleService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("High Achiever", response.getName());
    }

    @Test
    void shouldThrowWhenRuleNotFound() {
        when(ruleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ruleService.getById(999L));
    }

    @Test
    void shouldUpdateRule() {
        when(ruleRepository.findById(1L))
                .thenReturn(Optional.of(testRule));
        when(ruleRepository.save(any()))
                .thenReturn(testRule);

        RuleResponse response =
                ruleService.update(1L, ruleRequest);

        assertNotNull(response);
        verify(ruleRepository, times(1)).save(any());
        verify(ruleValidator, times(1)).validate(any());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentRule() {
        when(ruleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ruleService.update(999L, ruleRequest));
    }

    @Test
    void shouldActivateRule() {
        testRule.setActive(false);
        when(ruleRepository.findById(1L))
                .thenReturn(Optional.of(testRule));
        when(ruleRepository.save(any()))
                .thenReturn(testRule);

        ruleService.activate(1L);

        assertTrue(testRule.isActive());
        verify(ruleRepository, times(1)).save(testRule);
    }

    @Test
    void shouldDeactivateRule() {
        when(ruleRepository.findById(1L))
                .thenReturn(Optional.of(testRule));
        when(ruleRepository.save(any()))
                .thenReturn(testRule);

        ruleService.deactivate(1L);

        assertFalse(testRule.isActive());
        verify(ruleRepository, times(1)).save(testRule);
    }

    @Test
    void shouldThrowWhenActivatingNonExistentRule() {
        when(ruleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ruleService.activate(999L));
    }

    @Test
    void shouldThrowWhenDeactivatingNonExistentRule() {
        when(ruleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ruleService.deactivate(999L));
    }
}
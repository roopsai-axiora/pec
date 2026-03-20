package com.axiora.pec.evaluation.service;

import com.axiora.pec.audit.AuditService;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.evaluation.domain.EvaluationResult;
import com.axiora.pec.evaluation.domain.EvaluationStatus;
import com.axiora.pec.evaluation.dto.EvaluationRequest;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.engine.WeightedScoringEngine;
import com.axiora.pec.evaluation.repository.EvaluationResultRepository;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import com.axiora.pec.rule.engine.ConditionEvaluator;
import com.axiora.pec.rule.engine.DefaultRuleEngineImpl;
import com.axiora.pec.rule.repository.RuleRepository;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationResultRepository evaluationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EvaluationService evaluationService;

    private User testUser;
    private Goal testGoal;
    private KpiValue testKpi;
    private Rule testRule;
    private EvaluationRequest request;
    private EvaluationResult testResult;

    @BeforeEach
    void setUp() {
        // Use real implementations
        DefaultRuleEngineImpl ruleEngine =
                new DefaultRuleEngineImpl(
                        new ConditionEvaluator());
        WeightedScoringEngine scoringEngine =
                new WeightedScoringEngine();
        ObjectMapper objectMapper = new ObjectMapper();

        evaluationService = new EvaluationService(
                evaluationRepository,
                userRepository,
                goalRepository,
                kpiRepository,
                ruleRepository,
                ruleEngine,
                scoringEngine,
                auditService,
                objectMapper
        );

        testUser = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("password")
                .role(Role.EMPLOYEE)
                .build();

        testGoal = Goal.builder()
                .id(1L)
                .title("Improve Code Quality")
                .weightage(new BigDecimal("100.00"))
                .period("2026-Q1")
                .status(GoalStatus.ACTIVE)
                .assignedTo(testUser)
                .createdBy(testUser)
                .build();

        testKpi = KpiValue.builder()
                .id(1L)
                .goal(testGoal)
                .submittedBy(testUser)
                .targetValue(new BigDecimal("100.00"))
                .actualValue(new BigDecimal("95.00"))
                .period("2026-Q1")
                .build();

        testRule = Rule.builder()
                .id(1L)
                .name("High Achiever")
                .operator(RuleOperator.GT)
                .thresholdValue(new BigDecimal("90.00"))
                .action(RuleAction.ADD)
                .actionValue(new BigDecimal("10.00"))
                .priority(1)
                .active(true)
                .build();

        testResult = EvaluationResult.builder()
                .id(1L)
                .user(testUser)
                .evalPeriod("2026-Q1")
                .finalScore(new BigDecimal("100.00"))
                .status(EvaluationStatus.COMPLETED)
                .disqualified(false)
                .build();

        request = new EvaluationRequest(1L, "2026-Q1");
    }

    @Test
    void shouldEvaluateSuccessfully() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(ruleRepository
                .findByActiveTrueOrderByPriorityAsc())
                .thenReturn(List.of(testRule));
        when(goalRepository
                .findByAssignedToIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(List.of(testGoal));
        when(kpiRepository.findByGoalIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(Optional.of(testKpi));
        when(evaluationRepository
                .findByUserAndEvalPeriod(any(), any()))
                .thenReturn(Optional.empty());
        when(evaluationRepository.save(any()))
                .thenReturn(testResult);

        EvaluationResponse response =
                evaluationService.evaluate(request);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("2026-Q1", response.period());
        assertEquals(EvaluationStatus.COMPLETED,
                response.status());
        assertFalse(response.disqualified());
        verify(evaluationRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        EvaluationRequest badRequest =
                new EvaluationRequest(999L, "2026-Q1");

        assertThrows(ResourceNotFoundException.class,
                () -> evaluationService.evaluate(badRequest));
    }

    @Test
    void shouldThrowWhenNoGoalsFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(ruleRepository
                .findByActiveTrueOrderByPriorityAsc())
                .thenReturn(List.of(testRule));
        when(goalRepository
                .findByAssignedToIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> evaluationService.evaluate(request));
    }

    @Test
    void shouldUpsertWhenEvaluationExists() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(ruleRepository
                .findByActiveTrueOrderByPriorityAsc())
                .thenReturn(List.of(testRule));
        when(goalRepository
                .findByAssignedToIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(List.of(testGoal));
        when(kpiRepository.findByGoalIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(Optional.of(testKpi));
        when(evaluationRepository
                .findByUserAndEvalPeriod(any(), any()))
                .thenReturn(Optional.of(testResult));
        when(evaluationRepository.save(any()))
                .thenReturn(testResult);

        EvaluationResponse response =
                evaluationService.evaluate(request);

        assertNotNull(response);
        // Should update existing not create new
        verify(evaluationRepository, times(1)).save(any());
    }

    @Test
    void shouldDisqualifyWhenRuleDisqualifies() {
        Rule disqualifyRule = Rule.builder()
                .id(2L)
                .name("Low Performer")
                .operator(RuleOperator.LT)
                .thresholdValue(new BigDecimal("99.00"))
                .action(RuleAction.DISQUALIFY)
                .actionValue(BigDecimal.ZERO)
                .priority(1)
                .active(true)
                .build();

        EvaluationResult disqualifiedResult =
                EvaluationResult.builder()
                        .id(1L)
                        .user(testUser)
                        .evalPeriod("2026-Q1")
                        .finalScore(BigDecimal.ZERO)
                        .status(EvaluationStatus.DISQUALIFIED)
                        .disqualified(true)
                        .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(ruleRepository
                .findByActiveTrueOrderByPriorityAsc())
                .thenReturn(List.of(disqualifyRule));
        when(goalRepository
                .findByAssignedToIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(List.of(testGoal));
        when(kpiRepository.findByGoalIdAndPeriod(1L, "2026-Q1"))
                .thenReturn(Optional.of(testKpi));
        when(evaluationRepository
                .findByUserAndEvalPeriod(any(), any()))
                .thenReturn(Optional.empty());
        when(evaluationRepository.save(any()))
                .thenReturn(disqualifiedResult);

        EvaluationResponse response =
                evaluationService.evaluate(request);

        assertNotNull(response);
        assertTrue(response.disqualified());
        assertEquals(EvaluationStatus.DISQUALIFIED,
                response.status());
    }

    @Test
    void shouldGetEvaluationById() {
        when(evaluationRepository.findById(1L))
                .thenReturn(Optional.of(testResult));

        EvaluationResponse response =
                evaluationService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowWhenEvaluationNotFound() {
        when(evaluationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> evaluationService.getById(999L));
    }

    @Test
    void shouldGetEvaluationsByUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(evaluationRepository
                .findByUserOrderByEvaluatedAtDesc(any()))
                .thenReturn(List.of(testResult));

        List<EvaluationResponse> responses =
                evaluationService.getByUser(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
}
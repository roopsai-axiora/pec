package com.axiora.pec.evaluation.service;

import com.axiora.pec.audit.AuditAction;
import com.axiora.pec.audit.AuditService;
import com.axiora.pec.evaluation.domain.EvaluationResult;
import com.axiora.pec.evaluation.domain.EvaluationStatus;
import com.axiora.pec.evaluation.dto.EvaluationRequest;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import com.axiora.pec.evaluation.engine.ScoringEngine;
import com.axiora.pec.evaluation.repository.EvaluationResultRepository;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.engine.DefaultRuleEngineImpl;
import com.axiora.pec.rule.engine.RuleEngineResult;
import com.axiora.pec.rule.repository.RuleRepository;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EvaluationService {

    private final EvaluationResultRepository evaluationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final KpiRepository kpiRepository;
    private final RuleRepository ruleRepository;
    private final DefaultRuleEngineImpl ruleEngine;
    private final ScoringEngine scoringEngine;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public EvaluationService(
            EvaluationResultRepository evaluationRepository,
            UserRepository userRepository,
            GoalRepository goalRepository,
            KpiRepository kpiRepository,
            RuleRepository ruleRepository,
            DefaultRuleEngineImpl ruleEngine,
            ScoringEngine scoringEngine,
            AuditService auditService,
            ObjectMapper objectMapper) {
        this.evaluationRepository = evaluationRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.kpiRepository = kpiRepository;
        this.ruleRepository = ruleRepository;
        this.ruleEngine = ruleEngine;
        this.scoringEngine = scoringEngine;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EvaluationResponse evaluate(
            EvaluationRequest request) {

        log.info("Starting evaluation for user: {} period: {}",
                request.userId(), request.period());

        // Fetch user
        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", request.userId()));

        // Fetch active rules
        List<Rule> activeRules = ruleRepository
                .findByActiveTrueOrderByPriorityAsc();

        if (activeRules.isEmpty()) {
            log.warn("No active rules found for evaluation");
        }

        // Fetch goals for user and period
        List<Goal> goals = goalRepository
                .findByAssignedToIdAndPeriod(
                        user.getId(), request.period());

        if (goals.isEmpty()) {
            throw new IllegalArgumentException(
                    "No goals found for user: "
                            + user.getEmail()
                            + " in period: " + request.period());
        }

        // Process each goal
        List<GoalScoreDetail> goalDetails = new ArrayList<>();
        boolean anyDisqualified = false;

        for (Goal goal : goals) {

            // Fetch KPI for this goal and period
            Optional<KpiValue> kpiOpt = kpiRepository
                    .findByGoalIdAndPeriod(
                            goal.getId(), request.period());

            if (kpiOpt.isEmpty()) {
                log.warn("No KPI found for goal: {} period: {}",
                        goal.getTitle(), request.period());
                continue;
            }

            KpiValue kpi = kpiOpt.get();

            // Calculate achievement %
            BigDecimal achievement = BigDecimal.ZERO;
            if (kpi.getTargetValue()
                    .compareTo(BigDecimal.ZERO) > 0) {
                achievement = kpi.getActualValue()
                        .divide(kpi.getTargetValue(),
                                4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // Run rule engine
            RuleEngineResult ruleResult =
                    ruleEngine.evaluate(
                            achievement,
                            achievement,
                            activeRules);

            if (ruleResult.isDisqualified()) {
                anyDisqualified = true;
            }

            // Build goal score detail
            GoalScoreDetail detail = new GoalScoreDetail(
                    goal.getId(),
                    goal.getTitle(),
                    goal.getWeightage(),
                    kpi.getTargetValue(),
                    kpi.getActualValue(),
                    achievement,
                    ruleResult.getFinalScore(),
                    ruleResult.isDisqualified(),
                    ruleResult.getMatchedRule() != null
                            ? ruleResult.getMatchedRule().getName()
                            : "No rule matched",
                    ruleResult.getMatchedRule() != null
                            ? ruleResult.getMatchedRule()
                            .getAction().name()
                            : "N/A"
            );

            goalDetails.add(detail);
        }

        // Calculate final weighted score
        BigDecimal finalScore = anyDisqualified
                ? BigDecimal.ZERO
                : scoringEngine.calculate(goalDetails);

        EvaluationStatus status = anyDisqualified
                ? EvaluationStatus.DISQUALIFIED
                : EvaluationStatus.COMPLETED;

        // Build rule trace JSON
        String ruleTrace = buildRuleTrace(goalDetails);

        // Upsert evaluation result
        EvaluationResult result = evaluationRepository
                .findByUserAndEvalPeriod(user, request.period())
                .orElse(EvaluationResult.builder()
                        .user(user)
                        .evalPeriod(request.period())
                        .build());

        result.setFinalScore(finalScore);
        result.setStatus(status);
        result.setDisqualified(anyDisqualified);
        result.setRuleTrace(ruleTrace);
        result.setUpdatedAt(Instant.now());

        EvaluationResult saved =
                evaluationRepository.save(result);

        log.info("Evaluation complete. Score: {} Status: {}",
                finalScore, status);

        // Audit log
        auditService.log(
                AuditAction.EVALUATION_RUN,
                user.getId(),
                "EvaluationResult",
                saved.getId(),
                "Evaluation completed. Score: " + finalScore
        );

        return toResponse(saved, goalDetails);
    }

    public EvaluationResponse getById(Long id) {
        EvaluationResult result = evaluationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "EvaluationResult", id));
        return toResponse(result, List.of());
    }

    public List<EvaluationResponse> getByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", userId));

        return evaluationRepository
                .findByUserOrderByEvaluatedAtDesc(user)
                .stream()
                .map(r -> toResponse(r, List.of()))
                .toList();
    }

    private String buildRuleTrace(
            List<GoalScoreDetail> goalDetails) {
        try {
            return objectMapper
                    .writeValueAsString(goalDetails);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize rule trace", e);
            return "[]";
        }
    }

    private EvaluationResponse toResponse(
            EvaluationResult result,
            List<GoalScoreDetail> goalDetails) {
        return new EvaluationResponse(
                result.getId(),
                result.getUser().getId(),
                result.getUser().getFullName(),
                result.getEvalPeriod(),
                result.getFinalScore(),
                result.getStatus(),
                result.isDisqualified(),
                goalDetails,
                result.getEvaluatedAt()
        );
    }
}
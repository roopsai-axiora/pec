package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Default rule engine implementation.
 * Uses priority sort + first-match-wins strategy.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultRuleEngineImpl implements RuleEngine {

    private final ConditionEvaluator conditionEvaluator;

    @Override
    public RuleEngineResult evaluate(
            BigDecimal achievement,
            BigDecimal baseScore,
            List<Rule> rules) {

        log.debug("Evaluating rules for achievement: {}",
                achievement);

        if (rules == null || rules.isEmpty()) {
            log.debug("No rules to evaluate");
            return RuleEngineResult.noMatch(baseScore);
        }

        // Sort by priority ascending (lowest first)
        List<Rule> sorted = rules.stream()
                .filter(Rule::isActive)
                .sorted((a, b) ->
                        Integer.compare(a.getPriority(),
                                b.getPriority()))
                .toList();

        // First match wins
        for (Rule rule : sorted) {
            boolean matches = conditionEvaluator.evaluate(
                    achievement,
                    rule.getOperator(),
                    rule.getThresholdValue(),
                    rule.getThresholdValueUpper()
            );

            if (matches) {
                log.debug("Rule matched: {} (priority: {})",
                        rule.getName(), rule.getPriority());
                return applyAction(rule, baseScore);
            }
        }

        log.debug("No rule matched for achievement: {}",
                achievement);
        return RuleEngineResult.noMatch(baseScore);
    }

    private RuleEngineResult applyAction(
            Rule rule, BigDecimal baseScore) {

        RuleAction action = rule.getAction();
        BigDecimal actionValue = rule.getActionValue();

        BigDecimal finalScore = switch (action) {

            case ADD -> baseScore
                    .add(actionValue)
                    .setScale(2, RoundingMode.HALF_UP);

            case MULTIPLY -> baseScore
                    .multiply(actionValue)
                    .setScale(2, RoundingMode.HALF_UP);

            case SET_SCORE -> actionValue
                    .setScale(2, RoundingMode.HALF_UP);

            case DISQUALIFY -> {
                log.debug("Employee disqualified by rule: {}",
                        rule.getName());
                yield BigDecimal.ZERO;
            }
        };

        // Clamp score between 0 and 100
        finalScore = finalScore
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"));

        boolean disqualified =
                action == RuleAction.DISQUALIFY;

        return RuleEngineResult.builder()
                .finalScore(finalScore)
                .matchedRule(rule)
                .disqualified(disqualified)
                .description(buildDescription(
                        rule, baseScore, finalScore))
                .build();
    }

    private String buildDescription(
            Rule rule,
            BigDecimal baseScore,
            BigDecimal finalScore) {
        return String.format(
                "Rule '%s' matched. Action: %s %s. " +
                        "Score: %s → %s",
                rule.getName(),
                rule.getAction(),
                rule.getActionValue(),
                baseScore,
                finalScore
        );
    }
}
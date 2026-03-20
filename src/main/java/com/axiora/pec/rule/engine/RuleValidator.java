package com.axiora.pec.rule.engine;


import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleOperator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Validates rules before execution.
 * Pre-execution checks: null checks, BETWEEN bounds,
 * duplicate priority detection.
 */
@Component
public class RuleValidator {

    /**
     * Validates a single rule before saving.
     */
    public void validate(Rule rule) {
        validateNotNull(rule);
        validateName(rule);
        validateThresholds(rule);
        validateActionValue(rule);
        validatePriority(rule);
    }

    /**
     * Validates a list of rules before engine execution.
     */
    public void validateRules(List<Rule> rules) {
        if (rules == null || rules.isEmpty()) {
            return;
        }
        rules.forEach(this::validate);
        validateNoDuplicatePriorities(rules);
    }

    private void validateNotNull(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException(
                    "Rule cannot be null"
            );
        }
    }

    private void validateName(Rule rule) {
        if (rule.getName() == null
                || rule.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Rule name cannot be blank"
            );
        }
    }

    private void validateThresholds(Rule rule) {
        if (rule.getThresholdValue() == null) {
            throw new IllegalArgumentException(
                    "Rule threshold value cannot be null"
            );
        }

        if (rule.getThresholdValue()
                .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Rule threshold value cannot be negative"
            );
        }

        // BETWEEN requires upper bound
        if (rule.getOperator() == RuleOperator.BETWEEN) {
            if (rule.getThresholdValueUpper() == null) {
                throw new IllegalArgumentException(
                        "BETWEEN operator requires " +
                                "upper threshold value"
                );
            }

            // Upper bound must be greater than lower bound
            if (rule.getThresholdValueUpper()
                    .compareTo(rule.getThresholdValue()) <= 0) {
                throw new IllegalArgumentException(
                        "Upper threshold must be greater " +
                                "than lower threshold for BETWEEN operator"
                );
            }
        }
    }

    private void validateActionValue(Rule rule) {
        if (rule.getActionValue() == null) {
            throw new IllegalArgumentException(
                    "Rule action value cannot be null"
            );
        }
    }

    private void validatePriority(Rule rule) {
        if (rule.getPriority() < 1) {
            throw new IllegalArgumentException(
                    "Rule priority must be >= 1"
            );
        }
    }

    private void validateNoDuplicatePriorities(
            List<Rule> rules) {
        long distinctPriorities = rules.stream()
                .map(Rule::getPriority)
                .distinct()
                .count();

        if (distinctPriorities != rules.size()) {
            throw new IllegalArgumentException(
                    "Duplicate rule priorities detected. " +
                            "Each rule must have a unique priority."
            );
        }
    }
}
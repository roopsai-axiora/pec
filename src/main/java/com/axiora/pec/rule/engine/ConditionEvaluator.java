package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.RuleOperator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Stateless evaluator for rule conditions.
 * Supports GT, LT, EQ, GTE, LTE, BETWEEN operators.
 */
@Component
public class ConditionEvaluator {

    /**
     * Evaluates if a value matches a rule condition.
     *
     * @param value           actual KPI achievement value
     * @param operator        comparison operator
     * @param threshold       lower threshold value
     * @param thresholdUpper  upper threshold (BETWEEN only)
     * @return true if condition matches
     */
    public boolean evaluate(
            BigDecimal value,
            RuleOperator operator,
            BigDecimal threshold,
            BigDecimal thresholdUpper) {

        if (value == null || threshold == null) {
            return false;
        }

        int comparison = value.compareTo(threshold);

        return switch (operator) {
            case GT      -> comparison > 0;
            case LT      -> comparison < 0;
            case EQ      -> comparison == 0;
            case GTE     -> comparison >= 0;
            case LTE     -> comparison <= 0;
            case BETWEEN -> evaluateBetween(
                    value,
                    threshold,
                    thresholdUpper);
        };
    }

    private boolean evaluateBetween(
            BigDecimal value,
            BigDecimal lower,
            BigDecimal upper) {

        if (upper == null) {
            return false;
        }

        return value.compareTo(lower) >= 0
                && value.compareTo(upper) <= 0;
    }
}
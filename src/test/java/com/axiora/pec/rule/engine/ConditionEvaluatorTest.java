package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.RuleOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {

    private ConditionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ConditionEvaluator();
    }

    // ── GT Tests ─────────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueGreaterThanThreshold() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("95"),
                RuleOperator.GT,
                new BigDecimal("90"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueEqualToThresholdForGT() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("90"),
                RuleOperator.GT,
                new BigDecimal("90"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueLessThanThresholdForGT() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("85"),
                RuleOperator.GT,
                new BigDecimal("90"),
                null
        ));
    }

    // ── LT Tests ─────────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueLessThanThreshold() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("45"),
                RuleOperator.LT,
                new BigDecimal("50"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueEqualToThresholdForLT() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("50"),
                RuleOperator.LT,
                new BigDecimal("50"),
                null
        ));
    }

    // ── EQ Tests ─────────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueEqualsThreshold() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("80"),
                RuleOperator.EQ,
                new BigDecimal("80"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueNotEqualsThreshold() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("81"),
                RuleOperator.EQ,
                new BigDecimal("80"),
                null
        ));
    }

    // ── GTE Tests ────────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueGreaterThanOrEqualToThreshold() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("90"),
                RuleOperator.GTE,
                new BigDecimal("90"),
                null
        ));
    }

    @Test
    void shouldReturnTrueWhenValueGreaterForGTE() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("95"),
                RuleOperator.GTE,
                new BigDecimal("90"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueLessThanThresholdForGTE() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("89"),
                RuleOperator.GTE,
                new BigDecimal("90"),
                null
        ));
    }

    // ── LTE Tests ────────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueLessThanOrEqualToThreshold() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("50"),
                RuleOperator.LTE,
                new BigDecimal("50"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenValueGreaterThanThresholdForLTE() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("51"),
                RuleOperator.LTE,
                new BigDecimal("50"),
                null
        ));
    }

    // ── BETWEEN Tests ────────────────────────────────

    @Test
    void shouldReturnTrueWhenValueBetweenBounds() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("75"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90")
        ));
    }

    @Test
    void shouldReturnTrueWhenValueEqualsLowerBound() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("70"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90")
        ));
    }

    @Test
    void shouldReturnTrueWhenValueEqualsUpperBound() {
        assertTrue(evaluator.evaluate(
                new BigDecimal("90"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90")
        ));
    }

    @Test
    void shouldReturnFalseWhenValueBelowLowerBound() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("69"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90")
        ));
    }

    @Test
    void shouldReturnFalseWhenValueAboveUpperBound() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("91"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90")
        ));
    }

    @Test
    void shouldReturnFalseWhenUpperBoundNullForBETWEEN() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("75"),
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                null
        ));
    }

    // ── Null Tests ───────────────────────────────────

    @Test
    void shouldReturnFalseWhenValueIsNull() {
        assertFalse(evaluator.evaluate(
                null,
                RuleOperator.GT,
                new BigDecimal("90"),
                null
        ));
    }

    @Test
    void shouldReturnFalseWhenThresholdIsNull() {
        assertFalse(evaluator.evaluate(
                new BigDecimal("95"),
                RuleOperator.GT,
                null,
                null
        ));
    }
}
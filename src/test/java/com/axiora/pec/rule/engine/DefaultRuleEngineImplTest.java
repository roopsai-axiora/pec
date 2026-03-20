package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRuleEngineImplTest {

    private DefaultRuleEngineImpl ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new DefaultRuleEngineImpl(
                new ConditionEvaluator()
        );
    }

    // ── Helper methods ────────────────────────────────

    private Rule buildRule(
            String name,
            RuleOperator operator,
            BigDecimal threshold,
            BigDecimal upper,
            RuleAction action,
            BigDecimal actionValue,
            int priority) {
        return Rule.builder()
                .id((long) priority)
                .name(name)
                .operator(operator)
                .thresholdValue(threshold)
                .thresholdValueUpper(upper)
                .action(action)
                .actionValue(actionValue)
                .priority(priority)
                .active(true)
                .build();
    }

    // ── No Match Tests ────────────────────────────────

    @Test
    void shouldReturnBaseScoreWhenNoRules() {
        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of()
        );

        assertEquals(new BigDecimal("80"),
                result.getFinalScore());
        assertFalse(result.isDisqualified());
        assertNull(result.getMatchedRule());
    }

    @Test
    void shouldReturnBaseScoreWhenNoRuleMatches() {
        Rule rule = buildRule(
                "High Achiever",
                RuleOperator.GT,
                new BigDecimal("90"),
                null,
                RuleAction.ADD,
                new BigDecimal("10"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("85"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("80"),
                result.getFinalScore());
        assertNull(result.getMatchedRule());
    }

    // ── ADD Action Tests ──────────────────────────────

    @Test
    void shouldAddPointsWhenRuleMatches() {
        Rule rule = buildRule(
                "High Achiever",
                RuleOperator.GT,
                new BigDecimal("90"),
                null,
                RuleAction.ADD,
                new BigDecimal("10"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("90.00"),
                result.getFinalScore());
        assertFalse(result.isDisqualified());
        assertEquals("High Achiever",
                result.getMatchedRule().getName());
    }

    // ── MULTIPLY Action Tests ─────────────────────────

    @Test
    void shouldMultiplyScoreWhenRuleMatches() {
        Rule rule = buildRule(
                "Average Performer",
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90"),
                RuleAction.MULTIPLY,
                new BigDecimal("0.80"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("75"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("64.00"),
                result.getFinalScore());
        assertFalse(result.isDisqualified());
    }

    // ── SET_SCORE Action Tests ────────────────────────

    @Test
    void shouldSetScoreWhenRuleMatches() {
        Rule rule = buildRule(
                "Set Score Rule",
                RuleOperator.GTE,
                new BigDecimal("80"),
                null,
                RuleAction.SET_SCORE,
                new BigDecimal("75"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("85"),
                new BigDecimal("60"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("75.00"),
                result.getFinalScore());
        assertFalse(result.isDisqualified());
    }

    // ── DISQUALIFY Action Tests ───────────────────────

    @Test
    void shouldDisqualifyWhenRuleMatches() {
        Rule rule = buildRule(
                "Low Performer",
                RuleOperator.LT,
                new BigDecimal("50"),
                null,
                RuleAction.DISQUALIFY,
                new BigDecimal("0"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("45"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(BigDecimal.ZERO,
                result.getFinalScore());
        assertTrue(result.isDisqualified());
    }

    // ── Priority Tests ────────────────────────────────

    @Test
    void shouldEvaluateRulesInPriorityOrder() {
        Rule rule1 = buildRule(
                "High Achiever",
                RuleOperator.GT,
                new BigDecimal("90"),
                null,
                RuleAction.ADD,
                new BigDecimal("10"),
                1
        );

        Rule rule2 = buildRule(
                "Average Performer",
                RuleOperator.BETWEEN,
                new BigDecimal("70"),
                new BigDecimal("90"),
                RuleAction.MULTIPLY,
                new BigDecimal("0.80"),
                2
        );

        // Achievement 95 should match rule1 (priority 1)
        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of(rule2, rule1) // Reversed order
        );

        // Rule1 should win (priority 1 < priority 2)
        assertEquals("High Achiever",
                result.getMatchedRule().getName());
        assertEquals(new BigDecimal("90.00"),
                result.getFinalScore());
    }

    @Test
    void shouldApplyFirstMatchOnly() {
        Rule rule1 = buildRule(
                "Rule 1",
                RuleOperator.GT,
                new BigDecimal("50"),
                null,
                RuleAction.ADD,
                new BigDecimal("10"),
                1
        );

        Rule rule2 = buildRule(
                "Rule 2",
                RuleOperator.GT,
                new BigDecimal("80"),
                null,
                RuleAction.ADD,
                new BigDecimal("20"),
                2
        );

        // Achievement 95 matches both rules
        // But only rule1 (priority 1) should apply
        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of(rule1, rule2)
        );

        assertEquals("Rule 1",
                result.getMatchedRule().getName());
        assertEquals(new BigDecimal("90.00"),
                result.getFinalScore());
    }

    // ── Score Clamping Tests ──────────────────────────

    @Test
    void shouldClampScoreToMax100() {
        Rule rule = buildRule(
                "Big Bonus",
                RuleOperator.GT,
                new BigDecimal("90"),
                null,
                RuleAction.ADD,
                new BigDecimal("50"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("100"),
                result.getFinalScore());
    }

    @Test
    void shouldClampScoreToMin0() {
        Rule rule = buildRule(
                "Big Penalty",
                RuleOperator.LT,
                new BigDecimal("50"),
                null,
                RuleAction.ADD,
                new BigDecimal("-200"),
                1
        );

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("45"),
                new BigDecimal("80"),
                List.of(rule)
        );

        assertEquals(new BigDecimal("0"),
                result.getFinalScore());
    }

    // ── Inactive Rule Tests ───────────────────────────

    @Test
    void shouldSkipInactiveRules() {
        Rule inactiveRule = Rule.builder()
                .id(1L)
                .name("Inactive Rule")
                .operator(RuleOperator.GT)
                .thresholdValue(new BigDecimal("90"))
                .action(RuleAction.ADD)
                .actionValue(new BigDecimal("10"))
                .priority(1)
                .active(false)
                .build();

        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                List.of(inactiveRule)
        );

        assertNull(result.getMatchedRule());
        assertEquals(new BigDecimal("80"),
                result.getFinalScore());
    }

    // ── Null Rules Tests ──────────────────────────────

    @Test
    void shouldReturnBaseScoreWhenRulesAreNull() {
        RuleEngineResult result = ruleEngine.evaluate(
                new BigDecimal("95"),
                new BigDecimal("80"),
                null
        );

        assertEquals(new BigDecimal("80"),
                result.getFinalScore());
        assertFalse(result.isDisqualified());
    }
}
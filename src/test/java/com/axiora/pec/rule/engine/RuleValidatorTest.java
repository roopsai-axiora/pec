package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleValidatorTest {

    private RuleValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RuleValidator();
    }

    private Rule buildValidRule(int priority) {
        return Rule.builder()
                .id((long) priority)
                .name("Test Rule")
                .operator(RuleOperator.GT)
                .thresholdValue(new BigDecimal("90.00"))
                .action(RuleAction.ADD)
                .actionValue(new BigDecimal("10.00"))
                .priority(priority)
                .active(true)
                .build();
    }

    // ── Null Tests ────────────────────────────────────

    @Test
    void shouldThrowWhenRuleIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        Rule rule = buildValidRule(1);
        rule.setName(null);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Rule rule = buildValidRule(1);
        rule.setName("   ");

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenThresholdIsNull() {
        Rule rule = buildValidRule(1);
        rule.setThresholdValue(null);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenThresholdIsNegative() {
        Rule rule = buildValidRule(1);
        rule.setThresholdValue(new BigDecimal("-10.00"));

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenActionValueIsNull() {
        Rule rule = buildValidRule(1);
        rule.setActionValue(null);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    // ── Priority Tests ────────────────────────────────

    @Test
    void shouldThrowWhenPriorityIsZero() {
        Rule rule = buildValidRule(0);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenPriorityIsNegative() {
        Rule rule = buildValidRule(-1);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldPassWhenPriorityIsOne() {
        Rule rule = buildValidRule(1);

        assertDoesNotThrow(
                () -> validator.validate(rule));
    }

    // ── BETWEEN Tests ─────────────────────────────────

    @Test
    void shouldThrowWhenBetweenMissingUpperBound() {
        Rule rule = buildValidRule(1);
        rule.setOperator(RuleOperator.BETWEEN);
        rule.setThresholdValue(new BigDecimal("70.00"));
        rule.setThresholdValueUpper(null);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenBetweenUpperLessThanLower() {
        Rule rule = buildValidRule(1);
        rule.setOperator(RuleOperator.BETWEEN);
        rule.setThresholdValue(new BigDecimal("90.00"));
        rule.setThresholdValueUpper(new BigDecimal("70.00"));

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldThrowWhenBetweenUpperEqualsLower() {
        Rule rule = buildValidRule(1);
        rule.setOperator(RuleOperator.BETWEEN);
        rule.setThresholdValue(new BigDecimal("70.00"));
        rule.setThresholdValueUpper(new BigDecimal("70.00"));

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(rule));
    }

    @Test
    void shouldPassForValidBetweenRule() {
        Rule rule = buildValidRule(1);
        rule.setOperator(RuleOperator.BETWEEN);
        rule.setThresholdValue(new BigDecimal("70.00"));
        rule.setThresholdValueUpper(new BigDecimal("90.00"));

        assertDoesNotThrow(
                () -> validator.validate(rule));
    }

    // ── Duplicate Priority Tests ──────────────────────

    @Test
    void shouldThrowWhenDuplicatePriorities() {
        Rule rule1 = buildValidRule(1);
        Rule rule2 = buildValidRule(1);

        assertThrows(IllegalArgumentException.class,
                () -> validator.validateRules(
                        List.of(rule1, rule2)));
    }

    @Test
    void shouldPassWhenUniquePriorities() {
        Rule rule1 = buildValidRule(1);
        Rule rule2 = buildValidRule(2);
        Rule rule3 = buildValidRule(3);

        assertDoesNotThrow(
                () -> validator.validateRules(
                        List.of(rule1, rule2, rule3)));
    }

    @Test
    void shouldPassWhenRulesListIsEmpty() {
        assertDoesNotThrow(
                () -> validator.validateRules(List.of()));
    }

    @Test
    void shouldPassWhenRulesListIsNull() {
        assertDoesNotThrow(
                () -> validator.validateRules(null));
    }

    // ── Valid Rule Tests ──────────────────────────────

    @Test
    void shouldPassForValidGtRule() {
        Rule rule = buildValidRule(1);
        assertDoesNotThrow(
                () -> validator.validate(rule));
    }

    @Test
    void shouldPassForValidLtRule() {
        Rule rule = buildValidRule(1);
        rule.setOperator(RuleOperator.LT);

        assertDoesNotThrow(
                () -> validator.validate(rule));
    }

    @Test
    void shouldPassForValidDisqualifyRule() {
        Rule rule = buildValidRule(1);
        rule.setAction(RuleAction.DISQUALIFY);
        rule.setActionValue(BigDecimal.ZERO);

        assertDoesNotThrow(
                () -> validator.validate(rule));
    }
}
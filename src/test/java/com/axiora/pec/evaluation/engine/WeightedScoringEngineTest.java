package com.axiora.pec.evaluation.engine;

import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightedScoringEngineTest {

    private WeightedScoringEngine scoringEngine;

    @BeforeEach
    void setUp() {
        scoringEngine = new WeightedScoringEngine();
    }

    private GoalScoreDetail buildDetail(
            BigDecimal weightage,
            BigDecimal score,
            boolean disqualified) {
        return new GoalScoreDetail(
                1L,
                "Test Goal",
                weightage,
                new BigDecimal("100.00"),
                new BigDecimal("85.00"),
                new BigDecimal("85.00"),
                score,
                disqualified,
                "High Achiever",
                "ADD"
        );
    }

    @Test
    void shouldReturnZeroWhenNoGoals() {
        BigDecimal score = scoringEngine.calculate(
                List.of());
        assertEquals(BigDecimal.ZERO, score);
    }

    @Test
    void shouldReturnZeroWhenGoalsIsNull() {
        BigDecimal score = scoringEngine.calculate(null);
        assertEquals(BigDecimal.ZERO, score);
    }

    @Test
    void shouldCalculateWeightedScoreForSingleGoal() {
        GoalScoreDetail detail = buildDetail(
                new BigDecimal("100"),
                new BigDecimal("80.00"),
                false
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(detail));

        assertEquals(new BigDecimal("80.00"), score);
    }

    @Test
    void shouldCalculateWeightedScoreForMultipleGoals() {
        GoalScoreDetail goal1 = buildDetail(
                new BigDecimal("30"),
                new BigDecimal("90.00"),
                false
        );
        GoalScoreDetail goal2 = buildDetail(
                new BigDecimal("40"),
                new BigDecimal("75.00"),
                false
        );
        GoalScoreDetail goal3 = buildDetail(
                new BigDecimal("30"),
                new BigDecimal("80.00"),
                false
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(goal1, goal2, goal3));

        // (90*30 + 75*40 + 80*30) / 100 = 81.00
        assertEquals(new BigDecimal("81.00"), score);
    }

    @Test
    void shouldSkipDisqualifiedGoals() {
        GoalScoreDetail validGoal = buildDetail(
                new BigDecimal("60"),
                new BigDecimal("90.00"),
                false
        );
        GoalScoreDetail disqualifiedGoal = buildDetail(
                new BigDecimal("40"),
                new BigDecimal("0.00"),
                true
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(validGoal, disqualifiedGoal));

        assertEquals(new BigDecimal("90.00"), score);
    }

    @Test
    void shouldReturnZeroWhenAllGoalsDisqualified() {
        GoalScoreDetail disqualified1 = buildDetail(
                new BigDecimal("50"),
                new BigDecimal("0.00"),
                true
        );
        GoalScoreDetail disqualified2 = buildDetail(
                new BigDecimal("50"),
                new BigDecimal("0.00"),
                true
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(disqualified1, disqualified2));

        assertEquals(BigDecimal.ZERO, score);
    }

    @Test
    void shouldClampScoreToMax100() {
        GoalScoreDetail detail = buildDetail(
                new BigDecimal("100"),
                new BigDecimal("150.00"),
                false
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(detail));

        assertEquals(new BigDecimal("100"), score);
    }

    @Test
    void shouldClampScoreToMin0() {
        GoalScoreDetail detail = buildDetail(
                new BigDecimal("100"),
                new BigDecimal("-50.00"),
                false
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(detail));

        assertEquals(new BigDecimal("0"), score);
    }

    @Test
    void shouldHandleEqualWeights() {
        GoalScoreDetail goal1 = buildDetail(
                new BigDecimal("50"),
                new BigDecimal("80.00"),
                false
        );
        GoalScoreDetail goal2 = buildDetail(
                new BigDecimal("50"),
                new BigDecimal("60.00"),
                false
        );

        BigDecimal score = scoringEngine.calculate(
                List.of(goal1, goal2));

        assertEquals(new BigDecimal("70.00"), score);
    }
}
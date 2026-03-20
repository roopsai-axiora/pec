package com.axiora.pec.evaluation.engine;

import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import java.math.BigDecimal;
import java.util.List;

public interface ScoringEngine {

    /**
     * Calculates final weighted score from goal details.
     * Formula: Sum(score x weight) / Sum(weights)
     * Result clamped between 0 and 100.
     *
     * @param goalDetails  list of scored goals
     * @return             final weighted score
     */
    BigDecimal calculate(List<GoalScoreDetail> goalDetails);
}
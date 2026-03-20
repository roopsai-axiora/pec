package com.axiora.pec.evaluation.engine;

import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Weighted scoring engine implementation.
 * Formula: Sum(score x weight) / Sum(weights)
 * Score clamped between 0 and 100.
 */
@Component
public class WeightedScoringEngine implements ScoringEngine {

    @Override
    public BigDecimal calculate(
            List<GoalScoreDetail> goalDetails) {

        if (goalDetails == null || goalDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (GoalScoreDetail detail : goalDetails) {

            // Skip disqualified goals
            if (detail.disqualified()) {
                continue;
            }

            BigDecimal weightedScore = detail.score()
                    .multiply(detail.weightage())
                    .divide(new BigDecimal("100"),
                            4, RoundingMode.HALF_UP);

            totalWeightedScore = totalWeightedScore
                    .add(weightedScore);
            totalWeight = totalWeight
                    .add(detail.weightage());
        }

        // No valid goals
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal finalScore = totalWeightedScore
                .divide(totalWeight, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        // Clamp between 0 and 100
        return finalScore
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"));
    }
}
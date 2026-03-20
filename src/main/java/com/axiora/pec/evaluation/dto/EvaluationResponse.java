package com.axiora.pec.evaluation.dto;

import com.axiora.pec.evaluation.domain.EvaluationStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EvaluationResponse(
        Long id,
        Long userId,
        String userName,
        String period,
        BigDecimal finalScore,
        EvaluationStatus status,
        boolean disqualified,
        List<GoalScoreDetail> goalDetails,
        Instant evaluatedAt
) {}

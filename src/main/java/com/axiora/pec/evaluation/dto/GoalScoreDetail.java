package com.axiora.pec.evaluation.dto;

import java.math.BigDecimal;

public record GoalScoreDetail(
        Long goalId,
        String goalTitle,
        BigDecimal weightage,
        BigDecimal targetValue,
        BigDecimal actualValue,
        BigDecimal achievementPercent,
        BigDecimal score,
        boolean disqualified,
        String matchedRule,
        String ruleAction
) {}
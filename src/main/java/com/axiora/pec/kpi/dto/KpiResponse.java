package com.axiora.pec.kpi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record KpiResponse(
        Long id,
        Long goalId,
        String goalTitle,
        BigDecimal targetValue,
        BigDecimal actualValue,
        BigDecimal achievementPercent,
        String period,
        String notes,
        String submittedByName,
        Instant createdAt
) {}
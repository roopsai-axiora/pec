package com.axiora.pec.goal.dto;

import com.axiora.pec.goal.domain.GoalStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record GoalResponse(
        Long id,
        String title,
        String description,
        BigDecimal weightage,
        GoalStatus status,
        String period,
        String assignedToName,
        String assignedToEmail,
        Instant createdAt
) {}
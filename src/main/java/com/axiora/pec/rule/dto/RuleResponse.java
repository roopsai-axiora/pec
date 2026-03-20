package com.axiora.pec.rule.dto;

import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import java.math.BigDecimal;
import java.time.Instant;

public record RuleResponse(
        Long id,
        String name,
        String description,
        RuleOperator operator,
        BigDecimal thresholdValue,
        BigDecimal thresholdValueUpper,
        RuleAction action,
        BigDecimal actionValue,
        int priority,
        boolean active,
        Instant createdAt
) {}
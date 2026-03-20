package com.axiora.pec.rule.dto;

import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RuleRequest(

        @NotBlank(message = "Rule name is required")
        String name,

        String description,

        @NotNull(message = "Operator is required")
        RuleOperator operator,

        @NotNull(message = "Threshold value is required")
        @DecimalMin("0.00")
        BigDecimal thresholdValue,

        // Required only for BETWEEN operator
        BigDecimal thresholdValueUpper,

        @NotNull(message = "Action is required")
        RuleAction action,

        @NotNull(message = "Action value is required")
        BigDecimal actionValue,

        @Min(value = 1, message = "Priority must be >= 1")
        int priority
) {}
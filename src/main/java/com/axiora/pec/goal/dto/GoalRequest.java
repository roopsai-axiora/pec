package com.axiora.pec.goal.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record GoalRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("100.00")
        BigDecimal weightage,

        @NotBlank
        String period,

        @NotNull
        Long assignedToId
) {}
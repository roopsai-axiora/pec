package com.axiora.pec.evaluation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvaluationRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Period is required")
        String period
) {}
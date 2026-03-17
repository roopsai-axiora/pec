package com.axiora.pec.kpi.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record KpiRequest(

        @NotNull
        Long goalId,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal targetValue,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal actualValue,

        @NotBlank
        String period,

        String notes
) {}
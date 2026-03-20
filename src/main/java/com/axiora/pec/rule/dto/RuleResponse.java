package com.axiora.pec.rule.dto;

import com.axiora.pec.rule.domain.RuleAction;
import com.axiora.pec.rule.domain.RuleOperator;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponse implements Serializable {

    private Long id;
    private String name;
    private String description;
    private RuleOperator operator;
    private BigDecimal thresholdValue;
    private BigDecimal thresholdValueUpper;
    private RuleAction action;
    private BigDecimal actionValue;
    private int priority;
    private boolean active;
    private Instant createdAt;
}
package com.axiora.pec.rule.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "rules", indexes = {
        @Index(name = "idx_rule_priority",
                columnList = "priority"),
        @Index(name = "idx_rule_active",
                columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    /** Operator: GT, LT, EQ, GTE, LTE, BETWEEN */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator;

    /** Threshold value for comparison */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    /** Upper bound for BETWEEN operator */
    @Column(precision = 10, scale = 2)
    private BigDecimal thresholdValueUpper;

    /** Action to take when rule matches */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleAction action;

    /** Value used in action (points to add, multiplier, etc) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal actionValue;

    /** Lower priority number = evaluated first */
    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();
}
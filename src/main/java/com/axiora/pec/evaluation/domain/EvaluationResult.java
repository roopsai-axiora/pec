package com.axiora.pec.evaluation.domain;

import com.axiora.pec.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "evaluation_results", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_eval_user_period",
                columnNames = {"user_id", "eval_period"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "eval_period", nullable = false)
    private String evalPeriod;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EvaluationStatus status =
            EvaluationStatus.COMPLETED;

    @Column(columnDefinition = "TEXT")
    private String ruleTrace;

    @Column(nullable = false)
    @Builder.Default
    private boolean disqualified = false;

    @Column(updatable = false)
    @Builder.Default
    private Instant evaluatedAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();
}
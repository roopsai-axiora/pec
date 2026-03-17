package com.axiora.pec.kpi.domain;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "kpi_values", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_kpi_goal_period",
                columnNames = {"goal_id", "period"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal actualValue;

    @Column(nullable = false)
    private String period;

    @Column(length = 500)
    private String notes;

    @Column(updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();
}
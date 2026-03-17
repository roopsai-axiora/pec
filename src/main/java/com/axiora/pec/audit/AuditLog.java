package com.axiora.pec.audit;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user",
                columnList = "performed_by"),
        @Index(name = "idx_audit_action",
                columnList = "action"),
        @Index(name = "idx_audit_created",
                columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Column(name = "entity_type", length = 64)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 1000)
    private String details;

    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
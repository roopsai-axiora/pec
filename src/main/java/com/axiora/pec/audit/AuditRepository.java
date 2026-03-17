package com.axiora.pec.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPerformedBy(Long userId);

    Page<AuditLog> findByPerformedBy(
            Long userId, Pageable pageable);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByEntityTypeAndEntityId(
            String entityType, Long entityId);

    List<AuditLog> findByCreatedAtAfter(Instant since);
}
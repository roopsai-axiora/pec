package com.axiora.pec.audit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    // Fire and forget — runs in background thread
    // Must not block main transaction
    @Async
    public void log(AuditAction action,
                    Long performedBy,
                    String entityType,
                    Long entityId,
                    String details) {

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        auditRepository.save(auditLog);
    }

    public List<AuditLog> getByUser(Long userId) {
        return auditRepository.findByPerformedBy(userId);
    }

    public List<AuditLog> getByAction(AuditAction action) {
        return auditRepository.findByAction(action);
    }

    public List<AuditLog> getByEntity(String entityType,
                                      Long entityId) {
        return auditRepository
                .findByEntityTypeAndEntityId(
                        entityType, entityId);
    }
}
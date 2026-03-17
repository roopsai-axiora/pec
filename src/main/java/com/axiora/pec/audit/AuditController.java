package com.axiora.pec.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                auditService.getByUser(userId)
        );
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getByAction(
            @PathVariable AuditAction action) {
        return ResponseEntity.ok(
                auditService.getByAction(action)
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(
                auditService.getByEntity(entityType, entityId)
        );
    }
}
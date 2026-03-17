package com.axiora.pec.audit.service;

import com.axiora.pec.audit.AuditAction;
import com.axiora.pec.audit.AuditLog;
import com.axiora.pec.audit.AuditRepository;
import com.axiora.pec.audit.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void shouldSaveAuditLog() {
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .action(AuditAction.USER_REGISTERED)
                .performedBy(1L)
                .entityType("User")
                .entityId(1L)
                .details("User registered")
                .build();

        when(auditRepository.save(any()))
                .thenReturn(auditLog);

        auditService.log(
                AuditAction.USER_REGISTERED,
                1L,
                "User",
                1L,
                "User registered"
        );

        // Give async time to complete
        try { Thread.sleep(500); }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(auditRepository, times(1)).save(any());
    }

    @Test
    void shouldGetAuditLogsByUser() {
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .action(AuditAction.USER_REGISTERED)
                .performedBy(1L)
                .entityType("User")
                .entityId(1L)
                .build();

        when(auditRepository.findByPerformedBy(1L))
                .thenReturn(List.of(auditLog));

        List<AuditLog> logs =
                auditService.getByUser(1L);

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(AuditAction.USER_REGISTERED,
                logs.get(0).getAction());
    }

    @Test
    void shouldGetAuditLogsByAction() {
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .action(AuditAction.GOAL_CREATED)
                .performedBy(1L)
                .entityType("Goal")
                .entityId(1L)
                .build();

        when(auditRepository.findByAction(
                AuditAction.GOAL_CREATED))
                .thenReturn(List.of(auditLog));

        List<AuditLog> logs =
                auditService.getByAction(
                        AuditAction.GOAL_CREATED);

        assertNotNull(logs);
        assertEquals(1, logs.size());
    }

    @Test
    void shouldGetAuditLogsByEntity() {
        AuditLog auditLog = AuditLog.builder()
                .id(1L)
                .action(AuditAction.GOAL_CREATED)
                .performedBy(1L)
                .entityType("Goal")
                .entityId(1L)
                .build();

        when(auditRepository.findByEntityTypeAndEntityId(
                "Goal", 1L))
                .thenReturn(List.of(auditLog));

        List<AuditLog> logs =
                auditService.getByEntity("Goal", 1L);

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("Goal",
                logs.get(0).getEntityType());
    }
}
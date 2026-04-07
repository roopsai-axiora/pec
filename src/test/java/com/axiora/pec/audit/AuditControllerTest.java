package com.axiora.pec.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(auditController)
                .build();
    }

    @Test
    void shouldGetAuditLogsByUser() throws Exception {
        when(auditService.getByUser(1L))
                .thenReturn(List.of(buildAuditLog()));

        mockMvc.perform(get("/api/audit/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].performedBy").value(1))
                .andExpect(jsonPath("$[0].action").value("USER_REGISTERED"));
    }

    @Test
    void shouldGetAuditLogsByAction() throws Exception {
        when(auditService.getByAction(AuditAction.USER_LOGGED_IN))
                .thenReturn(List.of(buildAuditLog()));

        mockMvc.perform(get("/api/audit/action/USER_LOGGED_IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("User"));
    }

    @Test
    void shouldGetAuditLogsByEntity() throws Exception {
        when(auditService.getByEntity("User", 5L))
                .thenReturn(List.of(buildAuditLog()));

        mockMvc.perform(get("/api/audit/entity/User/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityId").value(1));
    }

    private AuditLog buildAuditLog() {
        return AuditLog.builder()
                .id(1L)
                .action(AuditAction.USER_REGISTERED)
                .performedBy(1L)
                .entityType("User")
                .entityId(1L)
                .details("User registered")
                .build();
    }
}

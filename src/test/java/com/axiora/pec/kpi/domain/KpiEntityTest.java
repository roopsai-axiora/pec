package com.axiora.pec.kpi.domain;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class KpiEntityTest {

    @Test
    void shouldCreateKpiWithDefaults() {
        KpiValue kpi = KpiValue.builder()
                .targetValue(new BigDecimal("100.00"))
                .actualValue(new BigDecimal("85.00"))
                .period("2026-Q1")
                .build();

        assertNotNull(kpi);
        assertNotNull(kpi.getCreatedAt());
        assertNotNull(kpi.getUpdatedAt());
    }

    @Test
    void shouldSetAndGetAllFields() {
        User user = User.builder()
                .id(1L)
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .build();

        Goal goal = Goal.builder()
                .id(1L)
                .title("Test Goal")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .status(GoalStatus.ACTIVE)
                .assignedTo(user)
                .createdBy(user)
                .build();

        KpiValue kpi = KpiValue.builder()
                .id(1L)
                .goal(goal)
                .submittedBy(user)
                .targetValue(new BigDecimal("100.00"))
                .actualValue(new BigDecimal("85.00"))
                .period("2026-Q1")
                .notes("Good progress")
                .build();

        assertEquals(1L, kpi.getId());
        assertEquals(goal, kpi.getGoal());
        assertEquals(user, kpi.getSubmittedBy());
        assertEquals(new BigDecimal("100.00"),
                kpi.getTargetValue());
        assertEquals(new BigDecimal("85.00"),
                kpi.getActualValue());
        assertEquals("2026-Q1", kpi.getPeriod());
        assertEquals("Good progress", kpi.getNotes());
    }

    @Test
    void shouldUpdateKpiValues() {
        KpiValue kpi = KpiValue.builder()
                .targetValue(new BigDecimal("100.00"))
                .actualValue(new BigDecimal("85.00"))
                .period("2026-Q1")
                .build();

        kpi.setActualValue(new BigDecimal("95.00"));
        kpi.setNotes("Updated progress");

        assertEquals(new BigDecimal("95.00"),
                kpi.getActualValue());
        assertEquals("Updated progress", kpi.getNotes());
    }
}
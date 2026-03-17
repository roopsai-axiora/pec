package com.axiora.pec.goal.domain;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GoalEntityTest {

    @Test
    void shouldCreateGoalWithDefaults() {
        Goal goal = Goal.builder()
                .title("Test Goal")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .build();

        assertNotNull(goal);
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
        assertNotNull(goal.getCreatedAt());
        assertNotNull(goal.getUpdatedAt());
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
                .title("Improve Quality")
                .description("Test coverage 80%")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .status(GoalStatus.ACTIVE)
                .assignedTo(user)
                .createdBy(user)
                .build();

        assertEquals(1L, goal.getId());
        assertEquals("Improve Quality", goal.getTitle());
        assertEquals("Test coverage 80%",
                goal.getDescription());
        assertEquals(new BigDecimal("30.00"),
                goal.getWeightage());
        assertEquals("2026-Q1", goal.getPeriod());
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
        assertEquals(user, goal.getAssignedTo());
        assertEquals(user, goal.getCreatedBy());
    }

    @Test
    void shouldUpdateGoalStatus() {
        Goal goal = Goal.builder()
                .title("Test Goal")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .build();

        goal.setStatus(GoalStatus.COMPLETED);
        assertEquals(GoalStatus.COMPLETED, goal.getStatus());

        goal.setStatus(GoalStatus.CANCELLED);
        assertEquals(GoalStatus.CANCELLED, goal.getStatus());
    }
}
package com.axiora.pec.goal.service;

import com.axiora.pec.audit.AuditService;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.common.exception.WeightageExceededException;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.goal.dto.GoalRequest;
import com.axiora.pec.goal.dto.GoalResponse;
import com.axiora.pec.goal.mapper.GoalMapper;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private GoalService goalService;

    private User testUser;
    private User managerUser;
    private Goal testGoal;
    private GoalRequest goalRequest;

    @BeforeEach
    void setUp() {
        managerUser = User.builder()
                .id(1L)
                .fullName("Manager One")
                .email("manager.one@axiora.com")
                .password("hashedPassword")
                .role(Role.MANAGER)
                .build();

        testUser = User.builder()
                .id(2L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .manager(managerUser)
                .build();

        testGoal = Goal.builder()
                .id(1L)
                .title("Improve Code Quality")
                .description("Increase test coverage")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .status(GoalStatus.ACTIVE)
                .assignedTo(testUser)
                .createdBy(managerUser)
                .build();

        goalRequest = new GoalRequest(
                "Improve Code Quality",
                "Increase test coverage",
                new BigDecimal("30.00"),
                "2026-Q1",
                2L
        );

        lenient().when(goalMapper.toResponse(any()))
                .thenReturn(new GoalResponse(
                        1L,
                        "Improve Code Quality",
                        "Increase test coverage",
                        new BigDecimal("30.00"),
                        GoalStatus.ACTIVE,
                        "2026-Q1",
                        "Roop Sai",
                        "roop@axiora.com",
                        Instant.now()
                ));
    }

    @Test
    void shouldCreateGoalSuccessfully() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(managerUser));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(testUser));
        when(goalRepository.sumWeightageByUserAndPeriod(
                any(), any()))
                .thenReturn(new BigDecimal("0.00"));
        when(goalRepository.save(any()))
                .thenReturn(testGoal);

        GoalResponse response =
                goalService.create(goalRequest, 1L);

        assertNotNull(response);
        assertEquals("Improve Code Quality", response.title());
        assertEquals(new BigDecimal("30.00"),
                response.weightage());
        verify(goalRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWeightageExceeds100() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(managerUser));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(testUser));
        when(goalRepository.sumWeightageByUserAndPeriod(
                any(), any()))
                .thenReturn(new BigDecimal("80.00"));

        assertThrows(WeightageExceededException.class,
                () -> goalService.create(goalRequest, 1L));

        verify(goalRepository, never()).save(any());
    }

    @Test
    void shouldRejectGoalCreationForEmployeeManagedByAnotherManager() {
        User otherManager = User.builder()
                .id(99L)
                .fullName("Other Manager")
                .email("other.manager@axiora.com")
                .password("hashedPassword")
                .role(Role.MANAGER)
                .build();
        User otherEmployee = User.builder()
                .id(3L)
                .fullName("Other Employee")
                .email("other.employee@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .manager(otherManager)
                .build();
        GoalRequest otherGoalRequest = new GoalRequest(
                "Improve Code Quality",
                "Increase test coverage",
                new BigDecimal("30.00"),
                "2026-Q1",
                3L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(managerUser));
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(otherEmployee));

        assertThrows(AccessDeniedException.class,
                () -> goalService.create(otherGoalRequest, 1L));
    }

    @Test
    void shouldGetGoalById() {
        when(goalRepository.findById(1L))
                .thenReturn(Optional.of(testGoal));

        GoalResponse response = goalService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Improve Code Quality", response.title());
    }

    @Test
    void shouldThrowExceptionWhenGoalNotFound() {
        when(goalRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> goalService.getById(999L));
    }

    @Test
    void shouldGetGoalsByUser() {
        when(goalRepository.findByAssignedToId(2L))
                .thenReturn(List.of(testGoal));

        List<GoalResponse> goals =
                goalService.getByUser(2L);

        assertNotNull(goals);
        assertEquals(1, goals.size());
        assertEquals("Improve Code Quality",
                goals.get(0).title());
    }

    @Test
    void shouldDeleteGoal() {
        when(goalRepository.findById(1L))
                .thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any()))
                .thenReturn(testGoal);

        goalService.delete(1L);

        assertEquals(GoalStatus.CANCELLED,
                testGoal.getStatus());
        verify(goalRepository, times(1)).save(testGoal);
    }

    @Test
    void shouldUpdateGoal() {
        when(goalRepository.findById(1L))
                .thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any()))
                .thenReturn(testGoal);

        GoalResponse response =
                goalService.update(1L, goalRequest);

        assertNotNull(response);
        verify(goalRepository, times(1)).save(any());
    }
}

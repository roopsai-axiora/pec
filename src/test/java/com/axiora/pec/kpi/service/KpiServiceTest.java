package com.axiora.pec.kpi.service;

import com.axiora.pec.audit.AuditService;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.dto.KpiRequest;
import com.axiora.pec.kpi.dto.KpiResponse;
import com.axiora.pec.kpi.mapper.KpiMapper;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    private KpiMapper kpiMapper;

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private KpiService kpiService;

    private User testUser;
    private Goal testGoal;
    private KpiValue testKpi;
    private KpiRequest kpiRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("hashedPassword")
                .role(Role.ADMIN)
                .build();

        testGoal = Goal.builder()
                .id(1L)
                .title("Improve Code Quality")
                .description("Increase test coverage")
                .weightage(new BigDecimal("30.00"))
                .period("2026-Q1")
                .status(GoalStatus.ACTIVE)
                .assignedTo(testUser)
                .createdBy(testUser)
                .build();

        testKpi = KpiValue.builder()
                .id(1L)
                .goal(testGoal)
                .submittedBy(testUser)
                .targetValue(new BigDecimal("100.00"))
                .actualValue(new BigDecimal("85.00"))
                .period("2026-Q1")
                .notes("Good progress")
                .build();

        kpiRequest = new KpiRequest(
                1L,
                new BigDecimal("100.00"),
                new BigDecimal("85.00"),
                "2026-Q1",
                "Good progress"
        );

        lenient().when(kpiMapper.toResponse(any()))
                .thenReturn(new KpiResponse(
                        1L,
                        1L,
                        "Improve Code Quality",
                        new BigDecimal("100.00"),
                        new BigDecimal("85.00"),
                        new BigDecimal("85.00"),
                        "2026-Q1",
                        "Good progress",
                        "Roop Sai",
                        Instant.now()
                ));
    }

    @Test
    void shouldCreateKpiSuccessfully() {
        when(goalRepository.findById(1L))
                .thenReturn(Optional.of(testGoal));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(kpiRepository.findByGoalIdAndPeriod(any(), any()))
                .thenReturn(Optional.empty());
        when(kpiRepository.save(any()))
                .thenReturn(testKpi);

        KpiResponse response =
                kpiService.upsert(kpiRequest, 1L);

        assertNotNull(response);
        assertEquals(1L, response.goalId());
        assertEquals(new BigDecimal("100.00"),
                response.targetValue());
        assertEquals(new BigDecimal("85.00"),
                response.actualValue());
        verify(kpiRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateExistingKpi() {
        when(goalRepository.findById(1L))
                .thenReturn(Optional.of(testGoal));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(kpiRepository.findByGoalIdAndPeriod(any(), any()))
                .thenReturn(Optional.of(testKpi));
        when(kpiRepository.save(any()))
                .thenReturn(testKpi);

        KpiResponse response =
                kpiService.upsert(kpiRequest, 1L);

        assertNotNull(response);
        verify(kpiRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenGoalNotFound() {
        when(goalRepository.findById(999L))
                .thenReturn(Optional.empty());

        KpiRequest badRequest = new KpiRequest(
                999L,
                new BigDecimal("100.00"),
                new BigDecimal("85.00"),
                "2026-Q1",
                "notes"
        );

        assertThrows(ResourceNotFoundException.class,
                () -> kpiService.upsert(badRequest, 1L));
    }

    @Test
    void shouldGetKpiById() {
        when(kpiRepository.findById(1L))
                .thenReturn(Optional.of(testKpi));

        KpiResponse response = kpiService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowExceptionWhenKpiNotFound() {
        when(kpiRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> kpiService.getById(999L));
    }

    @Test
    void shouldGetKpisByGoal() {
        when(kpiRepository.findByGoalId(1L))
                .thenReturn(List.of(testKpi));

        List<KpiResponse> kpis =
                kpiService.getByGoal(1L);

        assertNotNull(kpis);
        assertEquals(1, kpis.size());
    }

    @Test
    void shouldGetKpisByUser() {
        when(kpiRepository.findBySubmittedById(1L))
                .thenReturn(List.of(testKpi));

        List<KpiResponse> kpis =
                kpiService.getByUser(1L);

        assertNotNull(kpis);
        assertEquals(1, kpis.size());
    }

    @Test
    void shouldGetKpisByPeriod() {
        when(kpiRepository.findByPeriod("2026-Q1"))
                .thenReturn(List.of(testKpi));

        List<KpiResponse> kpis =
                kpiService.getByPeriod("2026-Q1");

        assertNotNull(kpis);
        assertEquals(1, kpis.size());
    }

    @Test
    void shouldCalculateAchievementPercent() {
        when(kpiRepository.findById(1L))
                .thenReturn(Optional.of(testKpi));

        KpiResponse response = kpiService.getById(1L);

        assertEquals(new BigDecimal("85.00"),
                response.achievementPercent());
    }
}
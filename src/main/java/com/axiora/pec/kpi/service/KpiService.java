package com.axiora.pec.kpi.service;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.dto.KpiRequest;
import com.axiora.pec.kpi.dto.KpiResponse;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class KpiService {

    private final KpiRepository kpiRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public KpiService(KpiRepository kpiRepository,
                      GoalRepository goalRepository,
                      UserRepository userRepository) {
        this.kpiRepository = kpiRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public KpiResponse upsert(KpiRequest request,
                              Long submittedById) {

        Goal goal = goalRepository
                .findById(request.goalId())
                .orElseThrow(() ->
                        new RuntimeException("Goal not found: "
                                + request.goalId()));

        User submittedBy = userRepository
                .findById(submittedById)
                .orElseThrow(() ->
                        new RuntimeException("User not found: "
                                + submittedById));

        // Upsert logic — update if exists, create if not
        KpiValue kpi = kpiRepository
                .findByGoalIdAndPeriod(
                        request.goalId(), request.period())
                .orElse(KpiValue.builder()
                        .goal(goal)
                        .submittedBy(submittedBy)
                        .period(request.period())
                        .build());

        kpi.setTargetValue(request.targetValue());
        kpi.setActualValue(request.actualValue());
        kpi.setNotes(request.notes());
        kpi.setUpdatedAt(Instant.now());

        return toResponse(kpiRepository.save(kpi));
    }

    public List<KpiResponse> getByGoal(Long goalId) {
        return kpiRepository.findByGoalId(goalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<KpiResponse> getByUser(Long userId) {
        return kpiRepository.findBySubmittedById(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public KpiResponse getById(Long id) {
        return toResponse(kpiRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("KPI not found: " + id)
                ));
    }

    public List<KpiResponse> getByPeriod(String period) {
        return kpiRepository.findByPeriod(period)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private KpiResponse toResponse(KpiValue kpi) {

        // Calculate achievement percentage
        BigDecimal achievement = BigDecimal.ZERO;
        if (kpi.getTargetValue()
                .compareTo(BigDecimal.ZERO) > 0) {
            achievement = kpi.getActualValue()
                    .divide(kpi.getTargetValue(),
                            4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return new KpiResponse(
                kpi.getId(),
                kpi.getGoal().getId(),
                kpi.getGoal().getTitle(),
                kpi.getTargetValue(),
                kpi.getActualValue(),
                achievement,
                kpi.getPeriod(),
                kpi.getNotes(),
                kpi.getSubmittedBy().getFullName(),
                kpi.getCreatedAt()
        );
    }
}
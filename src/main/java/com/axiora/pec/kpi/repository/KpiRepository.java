package com.axiora.pec.kpi.repository;

import com.axiora.pec.kpi.domain.KpiValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KpiRepository
        extends JpaRepository<KpiValue, Long> {

    List<KpiValue> findByGoalId(Long goalId);

    List<KpiValue> findBySubmittedById(Long userId);

    Optional<KpiValue> findByGoalIdAndPeriod(
            Long goalId, String period);

    List<KpiValue> findByPeriod(String period);
}
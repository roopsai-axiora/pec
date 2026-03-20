package com.axiora.pec.goal.repository;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByAssignedToId(Long userId);

    List<Goal> findByAssignedToIdAndStatus(
            Long userId, GoalStatus status);

    List<Goal> findByPeriod(String period);

    List<Goal> findByAssignedToIdAndPeriod(
            Long userId, String period);

    @Query("SELECT SUM(g.weightage) FROM Goal g " +
            "WHERE g.assignedTo.id = :userId " +
            "AND g.period = :period " +
            "AND g.status = 'ACTIVE'")
    java.math.BigDecimal sumWeightageByUserAndPeriod(
            Long userId, String period);
}
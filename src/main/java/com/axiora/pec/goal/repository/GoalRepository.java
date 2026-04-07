package com.axiora.pec.goal.repository;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.user.domain.User;
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

    List<Goal> findByAssignedToIdAndCreatedByIdOrderByCreatedAtDesc(
            Long assignedToId, Long createdById);

    boolean existsByIdAndCreatedById(Long id, Long createdById);

    @Query("""
            SELECT DISTINCT g.assignedTo FROM Goal g
            WHERE g.createdBy.id = :managerId
              AND g.assignedTo.role = com.axiora.pec.user.domain.Role.EMPLOYEE
              AND g.assignedTo.active = true
            ORDER BY g.assignedTo.fullName ASC
            """)
    List<User> findDistinctActiveEmployeesByCreatedByIdOrderByAssignedToFullNameAsc(
            Long managerId);

    @Query("""
            SELECT DISTINCT g.assignedTo FROM Goal g
            WHERE g.createdBy.id = :managerId
              AND g.assignedTo.role = com.axiora.pec.user.domain.Role.EMPLOYEE
              AND g.assignedTo.active = true
              AND (
                    LOWER(g.assignedTo.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(g.assignedTo.email) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY g.assignedTo.fullName ASC
            """)
    List<User> searchDistinctActiveEmployeesByCreatedById(
            Long managerId, String search);

    @Query("SELECT SUM(g.weightage) FROM Goal g " +
            "WHERE g.assignedTo.id = :userId " +
            "AND g.period = :period " +
            "AND g.status = 'ACTIVE'")
    java.math.BigDecimal sumWeightageByUserAndPeriod(
            Long userId, String period);
}

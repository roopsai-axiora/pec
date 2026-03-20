package com.axiora.pec.evaluation.repository;

import com.axiora.pec.evaluation.domain.EvaluationResult;
import com.axiora.pec.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationResultRepository
        extends JpaRepository<EvaluationResult, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<EvaluationResult> findByUserAndEvalPeriod(
            User user, String evalPeriod);

    @EntityGraph(attributePaths = {"user"})
    List<EvaluationResult> findByUserOrderByEvaluatedAtDesc(
            User user);

    @EntityGraph(attributePaths = {"user"})
    List<EvaluationResult> findByEvalPeriod(
            String evalPeriod);

    boolean existsByUserAndEvalPeriod(
            User user, String evalPeriod);
}
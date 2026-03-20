package com.axiora.pec.evaluation.repository;

import com.axiora.pec.evaluation.domain.EvaluationResult;
import com.axiora.pec.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationResultRepository
        extends JpaRepository<EvaluationResult, Long> {

    Optional<EvaluationResult> findByUserAndEvalPeriod(
            User user, String evalPeriod);

    List<EvaluationResult> findByUserOrderByEvaluatedAtDesc(
            User user);

    List<EvaluationResult> findByEvalPeriod(
            String evalPeriod);

    boolean existsByUserAndEvalPeriod(
            User user, String evalPeriod);
}
package com.axiora.pec.rule.repository;

import com.axiora.pec.rule.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository
        extends JpaRepository<Rule, Long> {

    List<Rule> findByActiveTrueOrderByPriorityAsc();

    Optional<Rule> findByPriority(int priority);

    boolean existsByPriority(int priority);

    List<Rule> findAllByOrderByPriorityAsc();
}
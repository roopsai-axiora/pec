package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.Rule;
import java.math.BigDecimal;
import java.util.List;

public interface RuleEngine {

    /**
     * Evaluates rules against an achievement value
     * and returns the final score.
     *
     * @param achievement  KPI achievement percentage (0-100)
     * @param baseScore    starting score before rules applied
     * @param rules        list of active rules ordered by priority
     * @return             RuleEngineResult with final score
     */
    RuleEngineResult evaluate(
            BigDecimal achievement,
            BigDecimal baseScore,
            List<Rule> rules);
}
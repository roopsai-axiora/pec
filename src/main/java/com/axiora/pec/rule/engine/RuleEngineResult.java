package com.axiora.pec.rule.engine;

import com.axiora.pec.rule.domain.Rule;
import lombok.*;
import java.math.BigDecimal;

/**
 * Result of rule engine evaluation.
 * Contains final score, matched rule, and disqualification status.
 */
@Getter
@Builder
public class RuleEngineResult {

    /** Final score after rule applied */
    private final BigDecimal finalScore;

    /** Rule that matched (null if no match) */
    private final Rule matchedRule;

    /** True if employee is disqualified */
    private final boolean disqualified;

    /** Human-readable description of what happened */
    private final String description;

    public static RuleEngineResult noMatch(
            BigDecimal baseScore) {
        return RuleEngineResult.builder()
                .finalScore(baseScore)
                .matchedRule(null)
                .disqualified(false)
                .description("No rule matched — " +
                        "base score retained")
                .build();
    }

    public static RuleEngineResult disqualified() {
        return RuleEngineResult.builder()
                .finalScore(BigDecimal.ZERO)
                .matchedRule(null)
                .disqualified(true)
                .description("Employee disqualified")
                .build();
    }
}
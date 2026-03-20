package com.axiora.pec.rule.domain;

public enum RuleAction {
    ADD,          // Add points to score
    MULTIPLY,     // Multiply score by factor
    SET_SCORE,    // Set score to specific value
    DISQUALIFY    // Set score to 0
}
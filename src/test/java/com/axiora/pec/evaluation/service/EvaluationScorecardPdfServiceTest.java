package com.axiora.pec.evaluation.service;

import com.axiora.pec.evaluation.domain.EvaluationStatus;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.dto.GoalScoreDetail;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationScorecardPdfServiceTest {

    @Test
    void shouldGenerateNonEmptyPdfBytes() {
        EvaluationScorecardPdfService pdfService = new EvaluationScorecardPdfService();
        EvaluationResponse response = new EvaluationResponse(
                1L,
                2L,
                "Jane Employee",
                "2026-Q1",
                new BigDecimal("88.50"),
                EvaluationStatus.COMPLETED,
                false,
                List.of(
                        new GoalScoreDetail(
                                10L,
                                "Improve Code Quality",
                                new BigDecimal("60.00"),
                                new BigDecimal("100.00"),
                                new BigDecimal("92.00"),
                                new BigDecimal("92.00"),
                                new BigDecimal("102.00"),
                                false,
                                "High Achiever Bonus",
                                "ADD"
                        )
                ),
                Instant.parse("2026-04-07T10:15:30Z")
        );

        byte[] pdf = pdfService.generate(response);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertEquals("%PDF", new String(pdf, 0, 4));
    }
}

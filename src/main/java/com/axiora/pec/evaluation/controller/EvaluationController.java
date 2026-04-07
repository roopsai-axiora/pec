package com.axiora.pec.evaluation.controller;

import com.axiora.pec.evaluation.dto.EvaluationRequest;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.service.EvaluationScorecardPdfService;
import com.axiora.pec.evaluation.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final EvaluationScorecardPdfService scorecardPdfService;

    public EvaluationController(
            EvaluationService evaluationService,
            EvaluationScorecardPdfService scorecardPdfService) {
        this.evaluationService = evaluationService;
        this.scorecardPdfService = scorecardPdfService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') and @accessControlService.isManagedEmployee(#request.userId)")
    public ResponseEntity<EvaluationResponse> evaluate(
            @Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(
                evaluationService.evaluate(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isEvaluationOwner(#id)")
    public ResponseEntity<EvaluationResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                evaluationService.getById(id)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isCurrentUser(#userId)")
    public ResponseEntity<List<EvaluationResponse>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                evaluationService.getByUser(userId)
        );
    }

    @GetMapping(value = "/{id}/scorecard", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("(hasRole('MANAGER') and @accessControlService.isEvaluationVisibleToManager(#id)) or (hasRole('EMPLOYEE') and @accessControlService.isEvaluationOwner(#id))")
    public ResponseEntity<byte[]> downloadScorecard(
            @PathVariable Long id) {
        EvaluationResponse evaluation = evaluationService.getById(id);
        byte[] pdf = scorecardPdfService.generate(evaluation);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"evaluation-scorecard-" + id + ".pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

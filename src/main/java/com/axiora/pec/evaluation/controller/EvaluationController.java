package com.axiora.pec.evaluation.controller;

import com.axiora.pec.evaluation.dto.EvaluationRequest;
import com.axiora.pec.evaluation.dto.EvaluationResponse;
import com.axiora.pec.evaluation.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(
            EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
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
}
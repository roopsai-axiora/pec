package com.axiora.pec.rule.controller;

import com.axiora.pec.rule.dto.RuleRequest;
import com.axiora.pec.rule.dto.RuleResponse;
import com.axiora.pec.rule.service.RuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RuleResponse> create(
            @Valid @RequestBody RuleRequest request) {
        return ResponseEntity.ok(
                ruleService.create(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RuleResponse>> getAll() {
        return ResponseEntity.ok(
                ruleService.getAll()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RuleResponse>> getActive() {
        return ResponseEntity.ok(
                ruleService.getActiveRules()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RuleResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ruleService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RuleRequest request) {
        return ResponseEntity.ok(
                ruleService.update(id, request)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(
            @PathVariable Long id) {
        ruleService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {
        ruleService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

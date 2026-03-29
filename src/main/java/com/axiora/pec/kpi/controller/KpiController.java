package com.axiora.pec.kpi.controller;

import com.axiora.pec.kpi.dto.KpiRequest;
import com.axiora.pec.kpi.dto.KpiResponse;
import com.axiora.pec.kpi.service.KpiService;
import com.axiora.pec.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isGoalOwner(#request.goalId)")
    public ResponseEntity<KpiResponse> upsert(
            @Valid @RequestBody KpiRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                kpiService.upsert(request, currentUser.getId())
        );
    }

    @GetMapping("/goal/{goalId}")
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isGoalOwner(#goalId)")
    public ResponseEntity<List<KpiResponse>> getByGoal(
            @PathVariable Long goalId) {
        return ResponseEntity.ok(
                kpiService.getByGoal(goalId)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isCurrentUser(#userId)")
    public ResponseEntity<List<KpiResponse>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                kpiService.getByUser(userId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') and @accessControlService.isKpiOwner(#id)")
    public ResponseEntity<KpiResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                kpiService.getById(id)
        );
    }

    @GetMapping("/period/{period}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<List<KpiResponse>> getByPeriod(
            @PathVariable String period) {
        return ResponseEntity.ok(
                kpiService.getByPeriod(period)
        );
    }
}
package com.axiora.pec.kpi.controller;

import com.axiora.pec.kpi.dto.KpiRequest;
import com.axiora.pec.kpi.dto.KpiResponse;
import com.axiora.pec.kpi.service.KpiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        return ResponseEntity.ok(
                kpiService.upsert(request, currentUserId)
        );
    }

    @GetMapping("/goal/{goalId}")
    @PreAuthorize("(hasRole('EMPLOYEE') and @accessControlService.isGoalOwner(#goalId)) or (hasRole('MANAGER') and @accessControlService.isGoalCreator(#goalId))")
    public ResponseEntity<List<KpiResponse>> getByGoal(
            @PathVariable Long goalId) {
        return ResponseEntity.ok(
                kpiService.getByGoal(goalId)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public ResponseEntity<List<KpiResponse>> getByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal(expression = "id") Long currentUserId,
            Authentication authentication) {
        Authentication resolvedAuthentication = authentication != null
                ? authentication
                : SecurityContextHolder.getContext().getAuthentication();
        boolean isManager = resolvedAuthentication != null
                && resolvedAuthentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_MANAGER".equals(authority.getAuthority()));

        return ResponseEntity.ok(
                isManager
                        ? kpiService.getByUserForManager(userId, currentUserId)
                        : kpiService.getByUser(userId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('EMPLOYEE') and @accessControlService.isKpiOwner(#id)) or (hasRole('MANAGER') and @accessControlService.isKpiVisibleToManager(#id))")
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

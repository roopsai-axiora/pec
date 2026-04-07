package com.axiora.pec.goal.controller;

import com.axiora.pec.goal.dto.GoalRequest;
import com.axiora.pec.goal.dto.GoalResponse;
import com.axiora.pec.goal.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GoalResponse> create(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        return ResponseEntity.ok(
                goalService.create(request, currentUserId)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public ResponseEntity<List<GoalResponse>> getByUser(
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
                        ? goalService.getByUserCreatedByManager(userId, currentUserId)
                        : goalService.getByUser(userId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('EMPLOYEE') and @accessControlService.isGoalOwner(#id)) or (hasRole('MANAGER') and @accessControlService.isGoalCreator(#id))")
    public ResponseEntity<GoalResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                goalService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<GoalResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(
                goalService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

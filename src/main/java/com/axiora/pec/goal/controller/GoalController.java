package com.axiora.pec.goal.controller;

import com.axiora.pec.goal.dto.GoalRequest;
import com.axiora.pec.goal.dto.GoalResponse;
import com.axiora.pec.goal.service.GoalService;
import com.axiora.pec.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<GoalResponse> create(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(
                goalService.create(request, currentUser.getId())
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalResponse>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                goalService.getByUser(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                goalService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(
                goalService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
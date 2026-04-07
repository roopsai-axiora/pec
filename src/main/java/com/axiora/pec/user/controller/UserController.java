package com.axiora.pec.user.controller;

import com.axiora.pec.user.dto.AuthResponse;
import com.axiora.pec.user.dto.LoginRequest;
import com.axiora.pec.user.dto.RegisterRequest;
import com.axiora.pec.user.dto.UserSummaryResponse;
import com.axiora.pec.user.auth.AuthenticatedUserPrincipal;
import com.axiora.pec.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                userService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                userService.login(request)
        );
    }

    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryResponse>> getEmployees(
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal(expression = "id") Long currentUserId,
            Authentication authentication) {
        Authentication resolvedAuthentication = authentication != null
                ? authentication
                : SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = resolvedAuthentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        Long resolvedUserId = currentUserId;
        if (resolvedUserId == null
                && resolvedAuthentication != null
                && resolvedAuthentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal) {
            resolvedUserId = principal.id();
        }

        return ResponseEntity.ok(userService.getEmployees(search, resolvedUserId, isAdmin));
    }
}

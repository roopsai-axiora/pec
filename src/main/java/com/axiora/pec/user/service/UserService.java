package com.axiora.pec.user.service;

import com.axiora.pec.audit.AuditAction;
import com.axiora.pec.audit.AuditService;
import com.axiora.pec.common.exception.EmailAlreadyExistsException;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.user.auth.AuthCacheService;
import com.axiora.pec.user.auth.JwtUtil;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.dto.AuthResponse;
import com.axiora.pec.user.dto.LoginRequest;
import com.axiora.pec.user.dto.RegisterRequest;
import com.axiora.pec.user.dto.UserSummaryResponse;
import com.axiora.pec.user.mapper.UserMapper;
import com.axiora.pec.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;
    private final UserMapper userMapper;
    private final AuthCacheService authCacheService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       AuditService auditService,
                       UserMapper userMapper,
                       AuthCacheService authCacheService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
        this.userMapper = userMapper;
        this.authCacheService = authCacheService;
    }

    public AuthResponse register(
            RegisterRequest request,
            Long actorUserId,
            boolean actorIsAdmin) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        validateRegistrationAccess(request, actorIsAdmin);
        User manager = resolveManager(request);

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .manager(manager)
                .build();

        userRepository.save(user);
        authCacheService.put(user);
        auditService.log(
                AuditAction.USER_REGISTERED,
                actorUserId != null ? actorUserId : user.getId(),
                "User",
                user.getId(),
                "User registered: " + user.getEmail()
        );

        if (isBootstrapAdminCreation(actorIsAdmin) && request.role() == Role.ADMIN) {
            String token = jwtUtil.generateToken(user);
            AuthResponse response = userMapper.toAuthResponse(user);
            return new AuthResponse(
                    token,
                    response.email(),
                    response.role()
            );
        }

        AuthResponse response = userMapper.toAuthResponse(user);
        return new AuthResponse(
                null,
                response.email(),
                response.role()
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
        authCacheService.put(user);

        auditService.log(
                AuditAction.USER_LOGGED_IN,
                user.getId(),
                "User",
                user.getId(),
                "User logged in: " + user.getEmail()
        );

        String token = jwtUtil.generateToken(user);

        AuthResponse response = userMapper.toAuthResponse(user);
        return new AuthResponse(
                token,
                response.email(),
                response.role()
        );
    }

    public void deactivate(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found", userId)
                );
        user.setActive(false);
        userRepository.save(user);
        authCacheService.evictByEmail(user.getEmail());
        auditService.log(
                AuditAction.USER_DEACTIVATED,
                userId,
                "User",
                userId,
                "User deactivated: " + user.getEmail()
        );
    }

    public List<UserSummaryResponse> getEmployees(
            String search, Long managerId, boolean isAdmin) {
        String normalizedSearch = search == null ? null : search.trim();
        List<User> employees;

        if (isAdmin) {
            employees = (normalizedSearch == null || normalizedSearch.isBlank())
                    ? userRepository.findByRoleAndActiveTrueOrderByFullNameAsc(Role.EMPLOYEE)
                    : userRepository.searchActiveUsersByRole(Role.EMPLOYEE, normalizedSearch);
        } else {
            employees = (normalizedSearch == null || normalizedSearch.isBlank())
                    ? userRepository.findByRoleAndActiveTrueAndManagerIdOrderByFullNameAsc(
                    Role.EMPLOYEE, managerId)
                    : userRepository.searchActiveUsersByRoleAndManagerId(
                    Role.EMPLOYEE, managerId, normalizedSearch);
        }

        return employees.stream()
                .map(user -> new UserSummaryResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.isActive()
                ))
                .toList();
    }

    private void validateRegistrationAccess(
            RegisterRequest request,
            boolean actorIsAdmin) {
        if (isBootstrapAdminCreation(actorIsAdmin)) {
            if (request.role() != Role.ADMIN) {
                throw new AccessDeniedException(
                        "The first account in the system must be an admin account"
                );
            }
            return;
        }

        if (!actorIsAdmin) {
            throw new AccessDeniedException(
                    "Only admins can create user accounts"
            );
        }
    }

    private boolean isBootstrapAdminCreation(boolean actorIsAdmin) {
        return !actorIsAdmin && userRepository.count() == 0;
    }

    private User resolveManager(RegisterRequest request) {
        if (request.role() == Role.EMPLOYEE) {
            if (request.managerId() == null) {
                throw new IllegalArgumentException(
                        "managerId is required when creating an employee account"
                );
            }

            User manager = userRepository.findById(request.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found", request.managerId()
                    ));

            if (manager.getRole() != Role.MANAGER || !manager.isActive()) {
                throw new IllegalArgumentException(
                        "managerId must reference an active manager"
                );
            }

            return manager;
        }

        if (request.managerId() != null) {
            throw new IllegalArgumentException(
                    "managerId is only allowed when creating an employee account"
            );
        }

        return null;
    }
}

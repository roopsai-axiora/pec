package com.axiora.pec.user.auth;

import com.axiora.pec.evaluation.domain.EvaluationResult;
import com.axiora.pec.evaluation.repository.EvaluationResultRepository;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private EvaluationResultRepository evaluationResultRepository;

    @Mock
    private UserRepository userRepository;

    private AccessControlService accessControlService;

    @BeforeEach
    void setUp() {
        accessControlService = new AccessControlService(
                goalRepository,
                kpiRepository,
                evaluationResultRepository,
                userRepository
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldMatchCurrentUserWhenPrincipalIsDomainUser() {
        User principal = domainUser(1L, "employee@axiora.com");
        setAuthentication(principal, principal.getAuthorities());

        assertTrue(accessControlService.isCurrentUser(1L));
        assertFalse(accessControlService.isCurrentUser(2L));
    }

    @Test
    void shouldResolveCurrentUserFromUserDetailsPrincipal() {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("employee@axiora.com")
                .password("secret")
                .authorities("ROLE_EMPLOYEE")
                .build();

        setAuthentication(principal, principal.getAuthorities());
        when(userRepository.findByEmail("employee@axiora.com"))
                .thenReturn(Optional.of(domainUser(9L, "employee@axiora.com")));

        assertTrue(accessControlService.isCurrentUser(9L));
    }

    @Test
    void shouldResolveCurrentUserFromStringPrincipal() {
        setAuthentication("employee@axiora.com",
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        when(userRepository.findByEmail("employee@axiora.com"))
                .thenReturn(Optional.of(domainUser(3L, "employee@axiora.com")));

        assertTrue(accessControlService.isCurrentUser(3L));
    }

    @Test
    void shouldReturnFalseForCurrentUserWhenAuthenticationMissing() {
        SecurityContextHolder.clearContext();

        assertFalse(accessControlService.isCurrentUser(1L));
    }

    @Test
    void shouldCheckGoalOwnership() {
        User principal = domainUser(5L, "employee@axiora.com");
        setAuthentication(principal, principal.getAuthorities());

        Goal ownedGoal = Goal.builder().id(10L).assignedTo(domainUser(5L, "employee@axiora.com")).build();
        Goal otherGoal = Goal.builder().id(11L).assignedTo(domainUser(8L, "other@axiora.com")).build();

        when(goalRepository.findById(10L)).thenReturn(Optional.of(ownedGoal));
        when(goalRepository.findById(11L)).thenReturn(Optional.of(otherGoal));
        when(goalRepository.findById(12L)).thenReturn(Optional.empty());

        assertTrue(accessControlService.isGoalOwner(10L));
        assertFalse(accessControlService.isGoalOwner(11L));
        assertFalse(accessControlService.isGoalOwner(12L));
    }

    @Test
    void shouldCheckKpiOwnership() {
        User principal = domainUser(5L, "employee@axiora.com");
        setAuthentication(principal, principal.getAuthorities());

        KpiValue ownedKpi = KpiValue.builder().id(10L).submittedBy(domainUser(5L, "employee@axiora.com")).build();
        KpiValue otherKpi = KpiValue.builder().id(11L).submittedBy(domainUser(8L, "other@axiora.com")).build();

        when(kpiRepository.findById(10L)).thenReturn(Optional.of(ownedKpi));
        when(kpiRepository.findById(11L)).thenReturn(Optional.of(otherKpi));
        when(kpiRepository.findById(12L)).thenReturn(Optional.empty());

        assertTrue(accessControlService.isKpiOwner(10L));
        assertFalse(accessControlService.isKpiOwner(11L));
        assertFalse(accessControlService.isKpiOwner(12L));
    }

    @Test
    void shouldCheckEvaluationOwnership() {
        User principal = domainUser(5L, "employee@axiora.com");
        setAuthentication(principal, principal.getAuthorities());

        EvaluationResult ownedResult = EvaluationResult.builder().id(10L).user(domainUser(5L, "employee@axiora.com")).build();
        EvaluationResult otherResult = EvaluationResult.builder().id(11L).user(domainUser(8L, "other@axiora.com")).build();

        when(evaluationResultRepository.findById(10L)).thenReturn(Optional.of(ownedResult));
        when(evaluationResultRepository.findById(11L)).thenReturn(Optional.of(otherResult));
        when(evaluationResultRepository.findById(12L)).thenReturn(Optional.empty());

        assertTrue(accessControlService.isEvaluationOwner(10L));
        assertFalse(accessControlService.isEvaluationOwner(11L));
        assertFalse(accessControlService.isEvaluationOwner(12L));
    }

    @Test
    void shouldReturnFalseWhenPrincipalIsAnonymousString() {
        setAuthentication("anonymousUser", List.of());

        assertFalse(accessControlService.isCurrentUser(1L));
    }

    private void setAuthentication(Object principal, List<?> authorities) {
        @SuppressWarnings("unchecked")
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "n/a",
                (List<SimpleGrantedAuthority>) authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User domainUser(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .fullName("Employee")
                .password("secret")
                .role(Role.EMPLOYEE)
                .build();
    }
}


package com.axiora.pec.user.auth;

import com.axiora.pec.evaluation.repository.EvaluationResultRepository;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.kpi.repository.KpiRepository;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AccessControlService {

    private final GoalRepository goalRepository;
    private final KpiRepository kpiRepository;
    private final EvaluationResultRepository evaluationResultRepository;
    private final UserRepository userRepository;

    public AccessControlService(
            GoalRepository goalRepository,
            KpiRepository kpiRepository,
            EvaluationResultRepository evaluationResultRepository,
            UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.kpiRepository = kpiRepository;
        this.evaluationResultRepository = evaluationResultRepository;
        this.userRepository = userRepository;
    }

    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    public boolean isGoalOwner(Long goalId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return goalRepository.findById(goalId)
                .map(goal -> goal.getAssignedTo().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isKpiOwner(Long kpiId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return kpiRepository.findById(kpiId)
                .map(kpi -> kpi.getSubmittedBy().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isEvaluationOwner(Long evaluationId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return evaluationResultRepository.findById(evaluationId)
                .map(result -> result.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        }

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .map(User::getId)
                    .orElse(null);
        }

        if (principal instanceof String username
                && !"anonymousUser".equals(username)) {
            return userRepository.findByEmail(username)
                    .map(User::getId)
                    .orElse(null);
        }

        return null;
    }
}


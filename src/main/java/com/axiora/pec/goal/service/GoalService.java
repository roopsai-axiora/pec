package com.axiora.pec.goal.service;

import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.common.exception.WeightageExceededException;
import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.domain.GoalStatus;
import com.axiora.pec.goal.dto.GoalRequest;
import com.axiora.pec.goal.dto.GoalResponse;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GoalResponse create(GoalRequest request,
                               Long createdById) {

        User assignedTo = userRepository
                .findById(request.assignedToId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: "
                                + request.assignedToId()));

        User createdBy = userRepository
                .findById(createdById)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Creator not found: "
                                + createdById));

        // Validate total weightage does not exceed 100
        BigDecimal existing = goalRepository
                .sumWeightageByUserAndPeriod(
                        request.assignedToId(), request.period());

        if (existing == null) existing = BigDecimal.ZERO;

        if (existing.add(request.weightage())
                .compareTo(new BigDecimal("100.00")) > 0) {
            throw new WeightageExceededException();
        }

        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .weightage(request.weightage())
                .period(request.period())
                .assignedTo(assignedTo)
                .createdBy(createdBy)
                .build();

        return toResponse(goalRepository.save(goal));
    }

    public List<GoalResponse> getByUser(Long userId) {
        return goalRepository
                .findByAssignedToId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GoalResponse getById(Long id) {
        return toResponse(goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found: ", id)
                ));
    }

    @Transactional
    public GoalResponse update(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found: ", id)
                );

        goal.setTitle(request.title());
        goal.setDescription(request.description());
        goal.setWeightage(request.weightage());
        goal.setPeriod(request.period());
        goal.setUpdatedAt(Instant.now());

        return toResponse(goalRepository.save(goal));
    }

    @Transactional
    public void delete(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Goal not found: ", id)
                );
        goal.setStatus(GoalStatus.CANCELLED);
        goal.setUpdatedAt(Instant.now());
        goalRepository.save(goal);
    }

    private GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getWeightage(),
                goal.getStatus(),
                goal.getPeriod(),
                goal.getAssignedTo().getFullName(),
                goal.getAssignedTo().getEmail(),
                goal.getCreatedAt()
        );
    }
}
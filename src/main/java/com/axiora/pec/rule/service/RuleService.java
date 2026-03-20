package com.axiora.pec.rule.service;

import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.domain.RuleOperator;
import com.axiora.pec.rule.dto.RuleRequest;
import com.axiora.pec.rule.dto.RuleResponse;
import com.axiora.pec.rule.repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    public RuleResponse create(RuleRequest request) {

        // Validate BETWEEN has upper bound
        if (request.operator() == RuleOperator.BETWEEN
                && request.thresholdValueUpper() == null) {
            throw new IllegalArgumentException(
                    "BETWEEN operator requires upper threshold"
            );
        }

        // Check duplicate priority
        if (ruleRepository.existsByPriority(
                request.priority())) {
            throw new IllegalArgumentException(
                    "Rule with priority "
                            + request.priority()
                            + " already exists"
            );
        }

        Rule rule = Rule.builder()
                .name(request.name())
                .description(request.description())
                .operator(request.operator())
                .thresholdValue(request.thresholdValue())
                .thresholdValueUpper(
                        request.thresholdValueUpper())
                .action(request.action())
                .actionValue(request.actionValue())
                .priority(request.priority())
                .build();

        return toResponse(ruleRepository.save(rule));
    }

    public List<RuleResponse> getAll() {
        return ruleRepository
                .findAllByOrderByPriorityAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RuleResponse> getActiveRules() {
        return ruleRepository
                .findByActiveTrueOrderByPriorityAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RuleResponse getById(Long id) {
        return toResponse(ruleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule", id)));
    }

    @Transactional
    public RuleResponse update(Long id,
                               RuleRequest request) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule", id));

        // Validate BETWEEN
        if (request.operator() == RuleOperator.BETWEEN
                && request.thresholdValueUpper() == null) {
            throw new IllegalArgumentException(
                    "BETWEEN operator requires upper threshold"
            );
        }

        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setOperator(request.operator());
        rule.setThresholdValue(request.thresholdValue());
        rule.setThresholdValueUpper(
                request.thresholdValueUpper());
        rule.setAction(request.action());
        rule.setActionValue(request.actionValue());
        rule.setPriority(request.priority());
        rule.setUpdatedAt(Instant.now());

        return toResponse(ruleRepository.save(rule));
    }

    @Transactional
    public void activate(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule", id));
        rule.setActive(true);
        rule.setUpdatedAt(Instant.now());
        ruleRepository.save(rule);
    }

    @Transactional
    public void deactivate(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule", id));
        rule.setActive(false);
        rule.setUpdatedAt(Instant.now());
        ruleRepository.save(rule);
    }

    private RuleResponse toResponse(Rule rule) {
        return new RuleResponse(
                rule.getId(),
                rule.getName(),
                rule.getDescription(),
                rule.getOperator(),
                rule.getThresholdValue(),
                rule.getThresholdValueUpper(),
                rule.getAction(),
                rule.getActionValue(),
                rule.getPriority(),
                rule.isActive(),
                rule.getCreatedAt()
        );
    }
}
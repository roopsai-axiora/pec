package com.axiora.pec.rule.service;

import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.rule.domain.Rule;
import com.axiora.pec.rule.dto.RuleRequest;
import com.axiora.pec.rule.dto.RuleResponse;
import com.axiora.pec.rule.engine.RuleValidator;
import com.axiora.pec.rule.repository.RuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RuleValidator ruleValidator;

    public RuleService(RuleRepository ruleRepository,
                       RuleValidator ruleValidator) {
        this.ruleRepository = ruleRepository;
        this.ruleValidator = ruleValidator;
    }

    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
    public RuleResponse create(RuleRequest request) {

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

        // Validate before saving
        ruleValidator.validate(rule);

        return toResponse(ruleRepository.save(rule));
    }

    public List<RuleResponse> getAll() {
        return ruleRepository
                .findAllByOrderByPriorityAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "rules", key = "'active'")
    public List<Rule> getActiveRulesFromCache() {
        return ruleRepository
                .findByActiveTrueOrderByPriorityAsc();
    }

    public List<RuleResponse> getActiveRules() {
        return getActiveRulesFromCache()
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
    @CacheEvict(value = "rules", allEntries = true)
    public RuleResponse update(Long id,
                               RuleRequest request) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rule", id));

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

        // Validate before saving
        ruleValidator.validate(rule);

        return toResponse(ruleRepository.save(rule));
    }

    @Transactional
    @CacheEvict(value = "rules", allEntries = true)
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
    @CacheEvict(value = "rules", allEntries = true)
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
        return RuleResponse.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .operator(rule.getOperator())
                .thresholdValue(rule.getThresholdValue())
                .thresholdValueUpper(rule.getThresholdValueUpper())
                .action(rule.getAction())
                .actionValue(rule.getActionValue())
                .priority(rule.getPriority())
                .active(rule.isActive())
                .createdAt(rule.getCreatedAt())
                .build();
    }
}
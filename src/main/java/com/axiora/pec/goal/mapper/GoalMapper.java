package com.axiora.pec.goal.mapper;

import com.axiora.pec.goal.domain.Goal;
import com.axiora.pec.goal.dto.GoalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "assignedToName",
            source = "assignedTo.fullName")
    @Mapping(target = "assignedToEmail",
            source = "assignedTo.email")
    GoalResponse toResponse(Goal goal);
}
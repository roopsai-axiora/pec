package com.axiora.pec.kpi.mapper;

import com.axiora.pec.kpi.domain.KpiValue;
import com.axiora.pec.kpi.dto.KpiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KpiMapper {

    @Mapping(target = "goalId",
            source = "goal.id")
    @Mapping(target = "goalTitle",
            source = "goal.title")
    @Mapping(target = "submittedByName",
            source = "submittedBy.fullName")
    @Mapping(target = "achievementPercent",
            ignore = true)
    KpiResponse toResponse(KpiValue kpiValue);
}
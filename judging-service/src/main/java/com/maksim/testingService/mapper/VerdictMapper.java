package com.maksim.testingService.mapper;

import com.maksim.common.event.SolutionJudgedEvent;
import com.maksim.testingService.service.model.VerdictInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VerdictMapper {
    SolutionJudgedEvent toEvent(VerdictInfo verdictInfo);
}

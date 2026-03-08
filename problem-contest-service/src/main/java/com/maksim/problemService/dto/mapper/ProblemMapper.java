package com.maksim.problemService.dto.mapper;

import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemSignature;
import com.maksim.problemService.entity.Problem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    ProblemSignature toProblemSignature(Problem p);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    Problem toEntity(ProblemCreateDto dto);
}

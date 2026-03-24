package com.maksim.problemService.dto.mapper;

import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.ProblemUpdateDto;
import com.maksim.problemService.entity.Problem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    ProblemSignatureResponseDto toProblemSignature(Problem p);

    ProblemResponseDto toResponseDto(Problem problem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    Problem toEntity(ProblemCreateDto dto);

    void updateFromPatch(@MappingTarget Problem problem, ProblemUpdateDto dto);
}

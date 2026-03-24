package com.maksim.problemService.dto.mapper;

import com.maksim.problemService.dto.contest.ContestResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.contest.UpdateContestDto;
import com.maksim.problemService.entity.Contest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContestMapper {

    @Mapping(target="id", ignore = true)
    @Mapping(target="authorId", ignore = true)
    Contest toEntity(CreateContestDto dto);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(@MappingTarget Contest entity, UpdateContestDto dto);

    ContestResponseDto toResponseDto(Contest contest);
}

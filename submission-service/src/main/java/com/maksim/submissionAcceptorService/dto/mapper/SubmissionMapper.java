package com.maksim.submissionAcceptorService.dto.mapper;

import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.event.SolutionSubmittedEvent;
import com.maksim.submissionAcceptorService.event.StandingsUpdateEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmissionMapper {


    @Mapping(target = "submissionId", source = "id")
    @Mapping(target = "language", source = "programmingLanguage")
    @Mapping(target = "timeLimit", ignore = true)
    @Mapping(target = "memoryLimit", ignore = true)
    @Mapping(target = "compilationTimeLimit", ignore = true)
    SolutionSubmittedEvent toSolutionSubmittedEvent(Submission s);


    @Mapping(target = "submissionTime", source = "time")
    StandingsUpdateEvent toStandingsUpdateEvent(Submission s);

    SubmissionDetailsResponseDto toSubmissionDetailsResponseDto(Submission s);

    SubmissionResponseDto toSubmissionResponseDto(Submission s);
}

package com.interviewprep.modules.Interview.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.interviewprep.modules.Interview.dto.InterviewSummaryDto;
import com.interviewprep.modules.Interview.entity.Interview;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    @Mapping(target = "interviewId", source = "id")
    @Mapping(target = "improvementSuggestions", ignore = true)
    @Mapping(target = "questionResults", ignore = true)
    InterviewSummaryDto toSummaryDto(Interview entity);
}

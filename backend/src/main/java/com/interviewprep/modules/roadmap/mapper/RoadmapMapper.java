package com.interviewprep.modules.roadmap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.roadmap.dto.RoadmapResponseDto;
import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;

@Mapper(componentModel = "spring")
public interface RoadmapMapper {
        
 
@Mapping(target = "weeklyPlan", ignore = true)
    @Mapping(target = "milestones", ignore = true)
    @Mapping(target = "recommendedProjects", ignore = true)
    @Mapping(target = "learningResources", ignore = true)
    @Mapping(target = "selectedDurationWeeks", ignore = true)
    RoadmapResponseDto toSummary(RoadmapTemplate entity);
}

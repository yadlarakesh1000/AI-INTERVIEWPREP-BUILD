package com.interviewprep.modules.skilltracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;
import com.interviewprep.modules.skilltracker.entity.DayProgress;

@Mapper(componentModel = "spring")
public interface SkillTrackerMapper {
  
     @Mapping(target = "topic", ignore = true)
    WeekProgressDto.DayDto toDayDto(DayProgress entity);
}

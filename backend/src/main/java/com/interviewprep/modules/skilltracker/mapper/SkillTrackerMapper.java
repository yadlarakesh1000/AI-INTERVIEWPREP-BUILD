package com.interviewprep.modules.skilltracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.skilltracker.dto.weekProgressDto;
import com.interviewprep.modules.skilltracker.entity.DayProgress;

@Mapper(componentModel = "spring")
public interface SkillTrackerMapper {
  
     @Mapping(target = "topic", ignore = true)
    weekProgressDto.DayDto tDayDto(DayProgress entity);
}

package com.interviewprep.modules.skilltracker.service;

import com.interviewprep.modules.skilltracker.dto.DayToggleRequest;
import com.interviewprep.modules.skilltracker.dto.SkillTrackerOverviewDto;
import com.interviewprep.modules.skilltracker.dto.weekProgressDto;

public interface SkillTrackerService {



     weekProgressDto getWeekProgress(Long userId,Integer weekNumber);
     weekProgressDto.DayDto toggleDay(Long userId,DayToggleRequest request);
     SkillTrackerOverviewDto getOverview(Long userId);
  
}

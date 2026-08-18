package com.interviewprep.modules.skilltracker.service;

import com.interviewprep.modules.skilltracker.dto.DayToggleRequest;
import com.interviewprep.modules.skilltracker.dto.SkillTrackerOverviewDto;
import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;

public interface SkillTrackerService {



     WeekProgressDto getWeekProgress(Long userId,Integer weekNumber);
     WeekProgressDto.DayDto toggleDay(Long userId,DayToggleRequest request);
     SkillTrackerOverviewDto getOverview(Long userId);
  
}

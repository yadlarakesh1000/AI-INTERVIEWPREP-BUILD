package com.interviewprep.modules.skilltracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillTrackerOverviewDto {
  
         private int totalWeeks;
         private int totalDays;
         private long completedDays;
         private double progressPercent;
         private int currentWeek;


}

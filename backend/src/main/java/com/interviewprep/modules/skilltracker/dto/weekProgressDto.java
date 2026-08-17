package com.interviewprep.modules.skilltracker.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class weekProgressDto {
     
  private int weekNumber;
  private String weekTitle;
  private List<DayDto> days;
  private double progressPercent;
  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DayDto{
    private int dayNumber;
    private String topic;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
  }
  
}

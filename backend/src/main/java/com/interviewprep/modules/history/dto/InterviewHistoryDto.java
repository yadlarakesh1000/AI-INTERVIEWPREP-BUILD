package com.interviewprep.modules.history.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewHistoryDto {
  
  private Long interviewId;
  private String interviewType;
  private String difficulty;
  private LocalDate date;
  private Integer durationSeconds;
  private double overallScore;
}

package com.interviewprep.modules.analytics.dto;

import java.time.LocalDate;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponseDto {
  
     private long totalInterviews;
     private double averageOverallScore;
     private double averageConfidenceScore;
     private String improvementStatus;
     private List<RecentScoreDto> recentScores;
     private List<String> latestSuggestions;

     @Builder
     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     public static class RecentScoreDto{
         private Long interviewId;
         private LocalDate date;
         private double overallScore;
         private double confidenceScore;
         private String type;
     } 
}

package com.interviewprep.modules.ai.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResult {
      
  private DimensionScore relevance;
  private DimensionScore clarityAndStructure;
  private DimensionScore depthAndAccuracy;
  private DimensionScore confidenceAndFluency;
  private double overallScore;
  private double confidenceScore;
  @Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
  public static class DimensionScore{
      int score;
      String feedback;

  }
}

package com.interviewprep.modules.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRecommendationResponse {
  
  private List<SkillSuggestion> recommendations;
  @Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
  public static class SkillSuggestion{
       private String skill;
       private String reason;
  }
}





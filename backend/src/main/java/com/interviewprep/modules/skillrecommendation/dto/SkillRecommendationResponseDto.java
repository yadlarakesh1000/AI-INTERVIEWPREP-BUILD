package com.interviewprep.modules.skillrecommendation.dto;

import java.util.List;

import com.interviewprep.modules.ai.dto.SkillRecommendationResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SkillRecommendationResponseDto {
  
  private List<String> currentSkills;
  private String careerPath;
  private List<SkillRecommendationResponse.SkillSuggestion> recommendations;
}

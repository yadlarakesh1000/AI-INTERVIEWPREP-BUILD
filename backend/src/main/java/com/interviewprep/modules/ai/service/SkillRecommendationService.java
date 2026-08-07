package com.interviewprep.modules.ai.service;

import com.interviewprep.modules.ai.dto.SkillRecommendationResponse;

public interface SkillRecommendationService {
  public SkillRecommendationResponse recommendSkills(String currentSkills, String careerPath);
}

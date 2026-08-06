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
public class ResumeData {
        
  private List<String> skills;
  private List<ProjectInfo> projects;
  private List<ExperienceInfo> experience;
  private String education;
  @Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
  public static class ProjectInfo{
        private String name;
        private String description;
        private List<String> technologies;
  }
  @Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
  public static class ExperienceInfo{
        private String role;
        private String company;
        private String duration;
        private String description;
  }
  
}

package com.interviewprep.modules.roadmap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoadmapResponseDto {
       private Long id;
       private String title;
       private String description;
       private String careerPath;
       private Integer durationWeeks;
       private JsonNode weeklyPlan;
       private JsonNode milestones;
       private JsonNode recommendedProjects;
       private JsonNode learningResources;
       private Integer selectedDurationWeeks;

}

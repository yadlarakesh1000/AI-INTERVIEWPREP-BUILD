package com.interviewprep.modules.roadmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapSelectionRequest {
      @NotNull(message = "roadmapTemplateId is required")
     private  Long roadmapTemplateId; 
     @NotNull(message="selectedDurationWeeks is required")
     private Integer selectedDurationWeeks;
}

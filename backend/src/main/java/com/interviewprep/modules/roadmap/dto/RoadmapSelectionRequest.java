package com.interviewprep.modules.roadmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class RoadmapSelectionRequest {
      @NotNull(message = "roadmapTemplateId is required")
     private  Long roadmapTemplateId; 
     @NotNull(message="selectedDurationWeeks is required")
     private Integer selectedDurationWeeks;
}

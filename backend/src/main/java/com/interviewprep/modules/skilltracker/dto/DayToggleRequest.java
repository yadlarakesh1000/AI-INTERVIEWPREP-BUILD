package com.interviewprep.modules.skilltracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DayToggleRequest {
  @NotNull
  @Min(value=1)
  private Integer weekNumber;
  @Min(value=1)
  @Max(value=7)
  private Integer dayNumber;
}

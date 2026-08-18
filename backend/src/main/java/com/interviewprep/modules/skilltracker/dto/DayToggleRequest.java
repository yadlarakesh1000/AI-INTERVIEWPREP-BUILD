package com.interviewprep.modules.skilltracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayToggleRequest {
  @NotNull
  @Min(value=1)
  private Integer weekNumber;
  @NotNull
  @Min(value=1)
  @Max(value=7)
  private Integer dayNumber;
}

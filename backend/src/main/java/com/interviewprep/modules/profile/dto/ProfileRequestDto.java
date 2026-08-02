package com.interviewprep.modules.profile.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestDto {
  @NotBlank(message = "Current Skills Required")
 private String currentSkills;
   @NotBlank(message = "Career Path is required")
   @Size(max=200,message = "career path not exceed 200 characters")
  private String careerPath;
   
      @NotNull(message = "Study duration (weeks) is required")
  private Integer studyDurationWeeks=26;
  
    @NotNull(message = "Daily Study hours are required")
    @Min(value=1,message="Daily study hours must be atleast 1")
    @Max(value=4,message = "Daily study hours not exceed 4")
  private Integer dailyStudyHours=1;

}

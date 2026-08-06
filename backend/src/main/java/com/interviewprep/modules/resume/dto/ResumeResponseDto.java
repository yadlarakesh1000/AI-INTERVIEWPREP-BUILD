package com.interviewprep.modules.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
 public class ResumeResponseDto {
       private Long id;
       private Long userId;
       private String fileName;
       private String extractedText;
       private String parsedData;
 }
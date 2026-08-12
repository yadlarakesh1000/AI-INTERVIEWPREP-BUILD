package com.interviewprep.modules.Interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InterviewResponseDto {
  private Long interviewId;
  private String sessionId;
  private String interviewType;
  private String difficulty;
  private int totalQuestions;
  private int currentQuestionNumber;
  private QuestionDto question;
  private String ttsAudioUrl;
}

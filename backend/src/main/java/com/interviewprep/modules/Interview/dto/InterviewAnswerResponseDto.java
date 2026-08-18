package com.interviewprep.modules.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interviewprep.modules.ai.dto.EvaluationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class InterviewAnswerResponseDto {
   private int questionNumber;
    private String transcribedAnswer;
    private EvaluationResult evaluation;
    private QuestionDto nextQuestion;
    private String ttsAudioUrl;
    @JsonProperty("isLastQuestion")
    private boolean lastQuestion;
}

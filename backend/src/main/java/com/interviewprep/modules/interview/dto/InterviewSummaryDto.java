package com.interviewprep.modules.interview.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSummaryDto {

    private Long interviewId;
    private String interviewType;
    private String difficulty;
    private int totalQuestions;
    private int durationSeconds;
    private double overallScore;
    private double confidenceScore;
    private List<String> improvementSuggestions;
    private List<QuestionResultDto> questionResults;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultDto {
        private int questionNumber;
        private String questionText;
        private double overallScore;
        private double confidenceScore;
    }
}
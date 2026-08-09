package com.interviewprep.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QAPair {

    private int questionNumber;
    private String question;
    private String answer;
    private EvaluationResult evaluation;
}

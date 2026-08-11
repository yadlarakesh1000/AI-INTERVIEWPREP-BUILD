package com.interviewprep.modules.interviewsession.model;

import com.interviewprep.modules.ai.dto.EvaluationResult;

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

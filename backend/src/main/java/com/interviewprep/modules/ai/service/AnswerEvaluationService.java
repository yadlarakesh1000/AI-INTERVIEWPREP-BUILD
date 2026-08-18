package com.interviewprep.modules.ai.service;

import java.util.List;

import com.interviewprep.modules.ai.dto.EvaluationResult;

public interface AnswerEvaluationService {
       public EvaluationResult evaluateAnswer(String question,String answer,String interviewType,String difficulty);
       public List<String> generateImprovementSuggestions(List<EvaluationResult> evaluations);
      
}


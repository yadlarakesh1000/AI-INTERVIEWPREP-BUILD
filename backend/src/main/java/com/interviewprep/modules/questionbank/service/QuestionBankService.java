package com.interviewprep.modules.questionbank.service;

import java.util.List;

import com.interviewprep.modules.questionbank.entity.HrQuestion;

public interface QuestionBankService {

  public HrQuestion getRandomQuestion(String difficulty,List<Long>excludeIds);

   List<HrQuestion> getQuestionsByCategory(String category, String difficulty);
}

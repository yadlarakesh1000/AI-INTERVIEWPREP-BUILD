package com.interviewprep.modules.ai.service;

import java.util.List;

import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.QAPair;

public interface InterviewQuestionService {
  
  public String generateQuestion(String interviewType,String difficulty,String topic,List<QAPair> history,ResumeData resumeData);
  
}

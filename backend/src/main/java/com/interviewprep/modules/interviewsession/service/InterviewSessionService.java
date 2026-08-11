package com.interviewprep.modules.interviewsession.service;

import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.InterviewSession;

public interface InterviewSessionService {
  InterviewSession createSession(Long interviewId, Long userId, String type, String difficulty, int totalQuestions, ResumeData resumeData, String csTopic);
  InterviewSession getSession(String sessionId);
  InterviewSession getByInterviewId(Long interviewId);
  void updateSession(String sessionId, InterviewSession session);
  void destroySession(String sessionId);
  boolean hasActiveSession(Long userId);

}

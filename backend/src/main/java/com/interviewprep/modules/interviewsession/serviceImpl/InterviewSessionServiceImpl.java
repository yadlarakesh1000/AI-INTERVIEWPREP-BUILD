package com.interviewprep.modules.interviewsession.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.InterviewSession;
import com.interviewprep.modules.interviewsession.service.InterviewSessionService;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterviewSessionServiceImpl implements InterviewSessionService{
    private static final long SESSION_MAX_HOURS= 2;
    private final ConcurrentHashMap<String, InterviewSession> activeSessions = new ConcurrentHashMap<>();
    @Override
    public InterviewSession createSession(Long interviewId, Long userId, String type, String difficulty,
        int totalQuestions, ResumeData resumeData, String csTopic) {
      String sessionId = UUID.randomUUID().toString();
     InterviewSession session =  InterviewSession.builder().sessionId(sessionId).interviewId(interviewId).userId(userId).interviewType(type).difficulty(difficulty).resumeData(resumeData).csTopic(csTopic).currentQuestionNumber(0).history(new ArrayList<>()).askedQuestionIds(new ArrayList<>()).startedAt(LocalDateTime.now()).build();
       activeSessions.put(sessionId,session);
       log.info("Created interview session {} for userId={} (interviewId={}, type={}, difficulty={})",
                sessionId, userId, interviewId, type, difficulty);
       return session;


    }
    @Override
    public InterviewSession getSession(String sessionId) {
        InterviewSession session = activeSessions.get(sessionId);
        if(session == null){
          throw new ResourceNotFoundException("InterviewSession","sessionId",sessionId);
        }
        return session;
    }
    @Override
  public InterviewSession getByInterviewId(Long interviewId) {

    if (interviewId == null) {
        throw new ResourceNotFoundException(
            "interviewSession",
            "InterviewId",
            interviewId
        );
    }

    for (InterviewSession session : activeSessions.values()) {
        if (interviewId.equals(session.getInterviewId())) {
            return session;
        }
    }

    throw new ResourceNotFoundException(
        "interviewSession",
        "InterviewId",
        interviewId
    );
}
     
    @Override
    public void updateSession(String sessionId, InterviewSession session) {
             activeSessions.put(sessionId, session);
           
    }
    @Override
    public void destroySession(String sessionId) {
         InterviewSession removed = activeSessions.remove(sessionId);
         if(removed != null){
           removed.setCurrentQuestionAudio(null);
            log.info("Destroyed interview session {}", sessionId);
         }
    }
    @Override
   public boolean hasActiveSession(Long userId) {

    if (userId == null) {
        return false;
    }

    for (InterviewSession session : activeSessions.values()) {
        if (userId.equals(session.getUserId())) {
            return true;
        }
    }

    return false;
}
     @Scheduled(fixedRate = 1_800_000)
    public void cleanupStaleSession(){
      LocalDateTime cutoff = LocalDateTime.now().minusHours(SESSION_MAX_HOURS);
      int before = activeSessions.size();
       activeSessions.entrySet().removeIf(entry -> {
        LocalDateTime startedAt = entry.getValue().getStartedAt();
        return startedAt !=null && startedAt.isBefore(cutoff);
       });
       int removed = before - activeSessions.size();
       if(removed>0){
        log.info("Stale session cleanup removed {} session(s); {} still active", removed, activeSessions.size());
       }
    }
    
  }



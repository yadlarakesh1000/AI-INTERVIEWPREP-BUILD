package com.interviewprep.modules.interviewsession.model;

import java.time.LocalDateTime;
import java.util.List;

import com.interviewprep.modules.ai.dto.ResumeData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {
     private String sessionId;
     private Long interviewId;
     private Long userId;
     private String interviewType;
     private String difficulty;
     private int totalQuestions;
     private int currentQuestionNumber;
     private List<QAPair> history;
     private List<Long> askedQuestionIds;
     private ResumeData resumeData;
     private String csTopic;
     private LocalDateTime startedAt;
     private byte[] currentQuestionAudio;  
}

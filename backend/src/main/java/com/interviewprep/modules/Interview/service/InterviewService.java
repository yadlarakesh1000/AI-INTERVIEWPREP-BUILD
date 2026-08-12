package com.interviewprep.modules.Interview.service;

import com.interviewprep.modules.Interview.dto.InterviewAnswerResponseDto;
import com.interviewprep.modules.Interview.dto.InterviewResponseDto;
import com.interviewprep.modules.Interview.dto.InterviewStartRequest;
import com.interviewprep.modules.Interview.dto.InterviewSummaryDto;

public interface InterviewService {
  
    InterviewResponseDto startInterview(Long userId,InterviewStartRequest request);
    InterviewAnswerResponseDto processAnswer(Long userId,Long interviewId, byte[]audioData,String filname);
    InterviewSummaryDto endInterview(Long userId,Long interviewId);



























}

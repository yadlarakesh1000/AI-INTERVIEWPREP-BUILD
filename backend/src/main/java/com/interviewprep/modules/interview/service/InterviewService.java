package com.interviewprep.modules.interview.service;

import com.interviewprep.modules.interview.dto.InterviewAnswerResponseDto;
import com.interviewprep.modules.interview.dto.InterviewResponseDto;
import com.interviewprep.modules.interview.dto.InterviewStartRequest;
import com.interviewprep.modules.interview.dto.InterviewSummaryDto;

public interface InterviewService {
  
    InterviewResponseDto startInterview(Long userId,InterviewStartRequest request);
    InterviewAnswerResponseDto processAnswer(Long userId,Long interviewId, byte[]audioData,String fileName);
    InterviewSummaryDto endInterview(Long userId,Long interviewId);



























}

package com.interviewprep.modules.history.service;

import java.util.List;

import com.interviewprep.modules.history.dto.InterviewHistoryDto;

public interface HistoryService {
      
  List<InterviewHistoryDto> getHistory(long userId);
  
}

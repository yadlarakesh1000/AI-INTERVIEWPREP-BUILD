package com.interviewprep.modules.analytics.service;

import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;

public interface AnalyticsService {
      
  AnalyticsResponseDto getAnalytics(Long userId);
}

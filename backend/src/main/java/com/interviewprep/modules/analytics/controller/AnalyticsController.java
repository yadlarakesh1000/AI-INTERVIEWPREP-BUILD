package com.interviewprep.modules.analytics.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;
import com.interviewprep.modules.analytics.service.AnalyticsService;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  @GetMapping
  public ResponseEntity<ApiResponse<AnalyticsResponseDto>> getAnalytics() {
      long userId = SecurityUtils.getCurrentUserId();
      AnalyticsResponseDto response = analyticsService.getAnalytics(userId);
      return ResponseEntity.ok(ApiResponse.success("Analytics fetched", response));
  }
  
}

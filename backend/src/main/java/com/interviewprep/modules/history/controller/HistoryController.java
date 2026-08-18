package com.interviewprep.modules.history.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.history.dto.InterviewHistoryDto;
import com.interviewprep.modules.history.service.HistoryService;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/api/history")
@RequiredArgsConstructor
@RestController
public class HistoryController {
    private final HistoryService historyService;
  @GetMapping
  public ResponseEntity<ApiResponse<List<InterviewHistoryDto>>> getHistory(){
      long userId = SecurityUtils.getCurrentUserId();
      List<InterviewHistoryDto> response = historyService.getHistory(userId);
      return ResponseEntity.ok(ApiResponse.success("Interview history fetched",response));
  }
  
}

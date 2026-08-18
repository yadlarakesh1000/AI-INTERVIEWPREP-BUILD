package com.interviewprep.modules.skilltracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.skilltracker.dto.DayToggleRequest;
import com.interviewprep.modules.skilltracker.dto.SkillTrackerOverviewDto;
import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;
import com.interviewprep.modules.skilltracker.service.SkillTrackerService;
import com.interviewprep.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/skill-tracker")
@RequiredArgsConstructor
public class SkillTrackerController {
     private final SkillTrackerService skillTrackerService;


     @GetMapping("/week/{weekNumber}")
     public ResponseEntity<ApiResponse<WeekProgressDto>> getWeek(@PathVariable Integer weekNumber) {
         Long userId = SecurityUtils.getCurrentUserId();
         return ResponseEntity.ok(ApiResponse.success("Week progress fetched", skillTrackerService.getWeekProgress(userId, weekNumber)));
     }
     @PutMapping("/toggle")
     public ResponseEntity<ApiResponse<WeekProgressDto.DayDto>>toggleDay (@Valid @RequestBody DayToggleRequest request) {
         Long userId = SecurityUtils.getCurrentUserId();
         return ResponseEntity.ok(ApiResponse.success("Day updated",
                skillTrackerService.toggleDay(userId, request)));
     }
      @GetMapping("/overview")
    public ResponseEntity<ApiResponse<SkillTrackerOverviewDto>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success("Overview fetched",
                skillTrackerService.getOverview(SecurityUtils.getCurrentUserId())));
    }
}



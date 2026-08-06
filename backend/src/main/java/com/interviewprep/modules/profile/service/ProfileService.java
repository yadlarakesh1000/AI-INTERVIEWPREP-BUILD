package com.interviewprep.modules.profile.service;

import org.springframework.stereotype.Service;

import com.interviewprep.modules.profile.dto.ProfileRequestDto;
import com.interviewprep.modules.profile.dto.ProfileResponseDto;


public interface ProfileService {
  ProfileResponseDto createProfile(Long userId, ProfileRequestDto request);
ProfileResponseDto getProfile(Long userId);
ProfileResponseDto updateProfile(Long userId, ProfileRequestDto request);

}

package com.interviewprep.modules.auth.service;

import org.springframework.stereotype.Service;

import com.interviewprep.modules.auth.dto.AuthResponse;
import com.interviewprep.modules.auth.dto.LoginRequest;
import com.interviewprep.modules.auth.dto.RefreshTokenRequest;
import com.interviewprep.modules.auth.dto.RegisterRequest;

@Service
public interface AuthService {
  
     AuthResponse register(RegisterRequest request);
     AuthResponse login(LoginRequest request);
     AuthResponse refreshToken(RefreshTokenRequest request);


}

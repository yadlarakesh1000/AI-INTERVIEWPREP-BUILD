package com.interviewprep.modules.auth.serviceImpli;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewprep.modules.auth.dto.AuthResponse;
import com.interviewprep.modules.auth.dto.LoginRequest;
import com.interviewprep.modules.auth.dto.RefreshTokenRequest;
import com.interviewprep.modules.auth.dto.RegisterRequest;
import com.interviewprep.modules.auth.entity.RefreshToken;
import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.repository.UserRepository;
import com.interviewprep.modules.auth.service.AuthService;
import com.interviewprep.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
      private UserRepository userRepository;
      private RefreshTokenRequest refreshTokenRequest;
      private JwtTokenProvider jwtTokenProvider;
      private PasswordEncoder passwordEncoder;
      private AuthenticationManager authenticationManager;


  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    
    if(Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))){
      log.warn("Registration attempt with existing email:{}",request.getEmail());
      throw new DataIntegrityViolationException("Email is already Registered");
    }
    User user =  User.builder()
                      .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName()).lastName(request.getLastName()).phone(request.getPhone()).isActive(true).build();
 User savedUser= userRepository.save(user);
 log.info("Registered new user id={} email={}", savedUser.getId(), savedUser.getEmail());
 String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getEmail(),savedUser.getId());
   
  }

  @Override
  public AuthResponse login(LoginRequest request) {
   
    throw new UnsupportedOperationException("Unimplemented method 'login'");
  }

  @Override
  public AuthResponse refreshToken(RefreshTokenRequest request) {
   
    throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
  }
  
}

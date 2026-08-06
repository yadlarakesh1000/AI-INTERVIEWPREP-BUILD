package com.interviewprep.modules.ai.config;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Value;

@Configuration
@Getter
public class GroqConfig {
    
  
  private String apiKey;
  private String baseUrl;
  private String model;
  private String whisperModel;

}

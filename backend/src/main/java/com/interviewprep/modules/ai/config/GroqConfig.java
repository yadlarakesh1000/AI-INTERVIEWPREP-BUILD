package com.interviewprep.modules.ai.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@Getter
public class GroqConfig {
    
  @Value("${app.groq.api-key:}")
  private String apiKey;
  @Value("${app.groq.base-url:https://api.groq.com/openai/v1}")
  private String baseUrl;
  @Value("${app.groq.model:llama-3.3-70b-versatile}")
  private String model;
  @Value("${app.groq.whisper-model:whisper-large-v3-turbo}")
  private String whisperModel;

  @Bean
  public RestTemplate groqRestTemplate(RestTemplateBuilder builder){
    return builder.setConnectTimeout(Duration.ofSeconds(30)).setReadTimeout(Duration.ofSeconds(30)).build();
  }
    @Bean
  public RestTemplate groqWhisperRestTemplate(RestTemplateBuilder builder){
    return builder.setConnectTimeout(Duration.ofSeconds(60)).setReadTimeout(Duration.ofSeconds(60)).build();
  }
}

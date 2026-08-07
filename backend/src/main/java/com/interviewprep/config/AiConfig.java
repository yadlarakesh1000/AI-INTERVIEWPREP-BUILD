package com.interviewprep.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@Getter
public class AiConfig {
   
  @Value("${app.gemini.api-key:}")
  private String geminiApiKey;

  @Bean
  public RestTemplate geminiRestTemplate(RestTemplateBuilder builder){
     return builder.setConnectTimeout(Duration.ofSeconds(30)).setReadTimeout(Duration.ofSeconds(30)).build();
  } 
}

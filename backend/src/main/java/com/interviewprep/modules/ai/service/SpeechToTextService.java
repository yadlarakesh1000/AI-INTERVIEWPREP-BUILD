package com.interviewprep.modules.ai.service;

public interface SpeechToTextService {
  public String transcribe(byte[] audioData, String fileName);
}

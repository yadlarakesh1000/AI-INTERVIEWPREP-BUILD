package com.interviewprep.modules.ai.service;

import com.interviewprep.modules.ai.dto.ResumeData;

public interface ResumeParsingService {
  ResumeData parseResumeText(String extractedText);
}

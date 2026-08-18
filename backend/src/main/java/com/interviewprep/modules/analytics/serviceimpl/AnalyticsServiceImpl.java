package com.interviewprep.modules.analytics.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto.RecentScoreDto;
import com.interviewprep.modules.analytics.service.AnalyticsService;
import com.interviewprep.modules.interview.entity.Interview;
import com.interviewprep.modules.interview.entity.InterviewStatus;
import com.interviewprep.modules.interview.repository.InterviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {
      private static final double STABLE_THRESHOLD=05;
      private final InterviewRepository interviewRepository;
      private final ObjectMapper objectMapper;

  @Override
  @Transactional(readOnly = true)
  public AnalyticsResponseDto getAnalytics(Long userId) {
    
     List<Interview> recent = interviewRepository.findTop5ByUserIdAndStatusOrderByCreatedAtDesc(userId,InterviewStatus.COMPLETED.name());
     long totalInterviews = interviewRepository.countByUserIdAndStatus(userId, InterviewStatus.COMPLETED.name());
      return AnalyticsResponseDto.builder().totalInterviews(totalInterviews)
      .averageOverallScore(round2(average(recent,true)))
      .averageConfidenceScore(round2(average(recent,false)))
      .improvementStatus(improvementStatus(recent))
      .recentScores(toRecentScores(recent))
      .latestSuggestions(latestSuggestions(recent))
      .build();

  }
 private String improvementStatus(List<Interview> newestFirst) {
        if (newestFirst.size() < 3) {
            return "NOT_ENOUGH_DATA";
        }
        List<Interview> last3 = newestFirst.subList(0, 3);
        List<Interview> previous = newestFirst.subList(3, newestFirst.size());
        if (previous.isEmpty()) {
            return "NOT_ENOUGH_DATA";
        }

        double lastAvg = average(last3, true);
        double previousAvg = average(previous, true);
        double diff = lastAvg - previousAvg;

        if (Math.abs(diff) <= STABLE_THRESHOLD) {
            return "STABLE";
        }
        return diff > 0 ? "IMPROVING" : "DECLINING";
    }

    private List<RecentScoreDto> toRecentScores(List<Interview> newestFirst) {
        
        List<Interview> chronological = new ArrayList<>(newestFirst);
        Collections.reverse(chronological);

        List<RecentScoreDto> scores = new ArrayList<>();
        for (Interview interview : chronological) {
            scores.add(RecentScoreDto.builder()
                    .interviewId(interview.getId())
                    .date(interview.getCreatedAt() == null ? null : interview.getCreatedAt().toLocalDate())
                    .overallScore(interview.getOverallScore() == null ? 0.0 : interview.getOverallScore().doubleValue())
                    .confidenceScore(interview.getConfidenceScore() == null ? 0.0 : interview.getConfidenceScore().doubleValue())
                    .type(interview.getInterviewType())
                    .build());
        }
        return scores;
    }

   
    private List<String> latestSuggestions(List<Interview> newestFirst) {
        if (newestFirst.isEmpty()) {
            return List.of();
        }
        String json = newestFirst.get(0).getImprovementSuggestions();
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            String[] parsed = objectMapper.readValue(json, String[].class);
            return List.of(parsed);
        } catch (Exception ex) {
            log.warn("Could not parse stored improvement suggestions; returning empty list", ex);
            return List.of();
        }
    }

  private double round2(double average) {
       return Math.round(average*100.0)/100.0;
  }

  private double average(List<Interview> recent, boolean overAll) {
double sum = 0.0;
int count = 0;

for (Interview interview : recent) {

    BigDecimal score;

    if (overAll == true) {
        score = interview.getOverallScore();
    } else {
        score = interview.getConfidenceScore();
    }

    if (score != null) {
        sum = sum + score.doubleValue();
        count = count + 1;
    }
}

if (count == 0) {
    return 0.0;
}

return sum / count;
  
}
}

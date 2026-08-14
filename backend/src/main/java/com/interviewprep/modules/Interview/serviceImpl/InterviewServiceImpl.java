package com.interviewprep.modules.Interview.serviceImpl;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.common.AppConstants;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.exception.UnauthorizedException;
import com.interviewprep.modules.Interview.Repository.InterviewRepository;
import com.interviewprep.modules.Interview.dto.InterviewAnswerResponseDto;
import com.interviewprep.modules.Interview.dto.InterviewResponseDto;
import com.interviewprep.modules.Interview.dto.InterviewStartRequest;
import com.interviewprep.modules.Interview.dto.InterviewSummaryDto;
import com.interviewprep.modules.Interview.dto.InterviewSummaryDto.QuestionResultDto;
import com.interviewprep.modules.Interview.dto.QuestionDto;
import com.interviewprep.modules.Interview.entity.Difficulty;
import com.interviewprep.modules.Interview.entity.Interview;
import com.interviewprep.modules.Interview.entity.InterviewStatus;
import com.interviewprep.modules.Interview.entity.InterviewType;
import com.interviewprep.modules.Interview.exception.InterviewException;
import com.interviewprep.modules.Interview.mapper.InterviewMapper;
import com.interviewprep.modules.Interview.service.InterviewService;
import com.interviewprep.modules.ai.dto.EvaluationResult;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.ai.service.AnswerEvaluationService;
import com.interviewprep.modules.ai.service.InterviewQuestionService;
import com.interviewprep.modules.ai.service.SpeechToTextService;
import com.interviewprep.modules.ai.service.TextToSpeechService;
import com.interviewprep.modules.interviewsession.model.InterviewSession;
import com.interviewprep.modules.interviewsession.model.QAPair;
import com.interviewprep.modules.interviewsession.service.InterviewSessionService;
import com.interviewprep.modules.profile.entity.UserProfile;
import com.interviewprep.modules.profile.repository.UserProfileRepository;
import com.interviewprep.modules.questionbank.entity.HrQuestion;
import com.interviewprep.modules.questionbank.service.QuestionBankService;
import com.interviewprep.modules.resume.Repository.ResumeRepository;
import com.interviewprep.modules.resume.entity.Resume;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

      private final InterviewRepository interviewRepository;
      private final InterviewSessionService interviewSessionService;
      private final InterviewQuestionService interviewQuestionService;
      private final AnswerEvaluationService answerEvaluationService;
      private final SpeechToTextService speechToTextService;
      private final TextToSpeechService textToSpeechService;
      private final QuestionBankService questionBankService;
      private final ResumeRepository resumeRepository;
      private final UserProfileRepository userProfileRepository;
      private final InterviewMapper interviewMapper;
      private final ObjectMapper objectMapper;

  @Override
  public InterviewResponseDto startInterview(Long userId, InterviewStartRequest request) {
    
      UserProfile profile = userProfileRepository.findByUserId(userId).orElseThrow(()-> new BadRequestException("Please complete your profile before starting an interview."));

        if (interviewRepository.findByUserIdAndStatus(userId, InterviewStatus.IN_PROGRESS.name()).isPresent()) {
            throw new InterviewException(
                    "You already have an interview in progress. Please end it before starting a new one.");
        }
            String type = normalizeType(request.getInterviewType());
            String difficulty = normalizeDifficulty(request.getDifficulty());
            int totalQuestions = request.getTotalQuestions();
            ResumeData resumeData=null;
            if(InterviewType.RESUME_BASED.name().equals(type)){
               resumeData = loadLatestResumeData(userId);
            }
            
          Interview interview =  Interview.builder().user(profile.getUser()).interviewType(type).difficulty(difficulty).totalQuestions(totalQuestions).status(InterviewStatus.IN_PROGRESS.name()).build();
            interview =interviewRepository.save(interview);
            InterviewSession session =interviewSessionService.createSession(interview.getId(), userId, type, difficulty, totalQuestions, resumeData, request.getCsTopic());
           String questionText = generateQuestionText(session,true); 
           int questionNumber =1;
           session.setCurrentQuestionNumber(questionNumber);
           session.getHistory().add(QAPair.builder().questionNumber(questionNumber).question(questionText).build());
           String audioUrl  = synthesizeInto(session,questionText,questionNumber);
           interviewSessionService.updateSession(session.getSessionId(), session);
            return InterviewResponseDto.builder().interviewId(interview.getId()).sessionId(session.getSessionId()).interviewType(type).difficulty(difficulty).totalQuestions(totalQuestions).currentQuestionNumber(questionNumber).ttsAudioUrl(audioUrl).build();
  }

  private String synthesizeInto(InterviewSession session, String questionText, int questionNumber) {
         byte[] audio = textToSpeechService.synthesize(questionText);
          session.setCurrentQuestionAudio(audio);
          if(audio == null || audio.length==0){
            return null;
          }
          return "/api/interviews/audio"+session.getSessionId()+"/q"+questionNumber;


  }

  private String generateQuestionText(InterviewSession session, boolean isFirst) {
                 
       String type = session.getInterviewType();
       boolean medium = Difficulty.MEDIUM.name().equals(session.getDifficulty());
       if(InterviewType.HR.name().equals(type)){
            if(medium && !isFirst){
                      return interviewQuestionService.generateQuestion(type,
                        session.getDifficulty(),null,session.getHistory(),null);
            }
            return pickHrQuestion(session);
       }
      List<QAPair> context;
           if (isFirst) {
            context = List.of();
                  } else {
            context = session.getHistory();
                  }
       return interviewQuestionService.generateQuestion(type, session.getDifficulty(), session.getCsTopic(), context, session.getResumeData());
  }
  private String pickHrQuestion(InterviewSession session) {
        HrQuestion question = questionBankService.getrandomQuestion(
                session.getDifficulty(), session.getAskedQuestionIds());
        if (question == null && Difficulty.MEDIUM.name().equals(session.getDifficulty())) {
            // Medium interviews may pull an Easy bank question for the opener (Section 15).
            question = questionBankService.getrandomQuestion(
                    Difficulty.EASY.name(), session.getAskedQuestionIds());
        }
        if (question == null) {
            throw new InterviewException("No HR questions are available. Please seed the question bank.");
        }
        session.getAskedQuestionIds().add(question.getId());
        return question.getQuestionText();
    }


  private ResumeData loadLatestResumeData(Long userId) {
    
       Resume resume = resumeRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElseThrow(()->new BadRequestException("please upload a resume first"));
       String parsed = resume.getParsedData();
       if(parsed == null || parsed.isBlank()){
        return null;
       }
         try {
            return objectMapper.readValue(parsed, ResumeData.class);
        } catch (Exception ex) {
            log.warn("Could not parse stored resume data for userId={}; proceeding without it", userId, ex);
            return null;
        }
  }

 private String normalizeDifficulty(String difficulty) {
        if (difficulty == null) {
            throw new BadRequestException("difficulty is required.");
        }
        String normalized = difficulty.trim().toUpperCase();
        for (Difficulty d : Difficulty.values()) {
            if (d.name().equals(normalized)) {
                return normalized;
            }
        }
        throw new BadRequestException("Invalid difficulty: " + difficulty);
    }

  private String normalizeType(String type) {
        if(type == null ){
              throw new BadRequestException("interview type required");
        }
        String normalized = type.trim().toUpperCase();
        for(InterviewType t : InterviewType.values()){
             if(t.name().equals(normalized)) return normalized;
        }
        throw new BadRequestException("Invalid interview Type"+type);
  }
 @Override
  public InterviewAnswerResponseDto processAnswer(Long userId, Long interviewId, byte[] audioData, String filname) {
              InterviewSession session = interviewSessionService.getByInterviewId(interviewId);
              verifyOwnerShip(session,userId);
              if(session.getHistory().isEmpty()){
                throw new InterviewException("No actvie question to answer for this interview");
              }
              QAPair pending  = session.getHistory().get(session.getHistory().size()-1);
              int answeredNumber = pending.getQuestionNumber();
              String transcrpit = speechToTextService.transcribe(audioData, filname);
              EvaluationResult evaluation = answerEvaluationService.evaluateAnswer(pending.getQuestion(), transcrpit, session.getInterviewType(), session.getDifficulty());
              pending.setAnswer(transcrpit);
              pending.setEvaluation(evaluation);
              boolean isLastQuestion = answeredNumber >= session.getTotalQuestions();
              QuestionDto nextQuestion = null;
              String audioUrl = null;
              if(!isLastQuestion){
                String nextText = generateQuestionText(session, false);
                int nextNumber = answeredNumber+1;
                session.setCurrentQuestionNumber(nextNumber);
                session.getHistory().add(QAPair.builder().questionNumber(nextNumber).question(nextText).build());
              
              audioUrl = synthesizeInto(session, nextText,answeredNumber);
              nextQuestion = QuestionDto.builder().questionNumber(nextNumber).text(nextText).build();
              }
              else{
                session.setCurrentQuestionAudio(null);
              }
              interviewSessionService.updateSession(session.getSessionId(), session);
              return InterviewAnswerResponseDto.builder().questionNumber(answeredNumber).transcribedAnswer(transcrpit).evaluation(evaluation).nextQuestion(nextQuestion).ttsAudioUrl(audioUrl).lastQuestion(isLastQuestion).build();
  }
  

  private void verifyOwnerShip(InterviewSession session, Long userId) {
                if(!session.getUserId().equals(userId)){
                throw new UnauthorizedException("You are not authorized to access this interview.");  
                }
}

  @Override
  public InterviewSummaryDto endInterview(Long userId, Long interviewId) {
    
        InterviewSession session = interviewSessionService.getByInterviewId(interviewId);
        verifyOwnerShip(session, userId);

Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));
                List<QAPair> answered = new ArrayList<>();
                for(QAPair q: session.getHistory()){
                  if(q.getEvaluation()!=null){
                    answered.add(q);
                  }
                }
                double  overallScore = averageOverall(answered);
                double confidenceScore = averageConfidenceScore(answered);
                List<EvaluationResult> evalations = new ArrayList<>();
                for(QAPair q : answered){
                    evalations.add(q.getEvaluation());
                }
                List<String> suggestions = answerEvaluationService.generateImprovementSuggestionsList(evalations);

            int durationSeconds =  (int)Duration.between(session.getStartedAt(),LocalDateTime.now()).getSeconds();
            interview.setStatus(InterviewStatus.COMPLETED.name());
            interview.setOverallScore(BigDecimal.valueOf(overallScore));
            interview.setConfidenceScore(BigDecimal.valueOf(confidenceScore));
            interview.setDurationSeconds(durationSeconds);
            interview.setImprovementSuggestions(toJson(suggestions));
            interview = interviewRepository.save(interview);
            enforceHistoryLimit(userId);
            interviewSessionService.destroySession(session.getSessionId());
            log.info("Ended interview {} for userId={} (overall={}, confidence={}, {} answered)",
                interviewId, userId, overallScore, confidenceScore, answered.size());
           InterviewSummaryDto summary = interviewMapper.toSummaryDto(interview);
           summary.setImprovementSuggestions(suggestions);
           summary.setQuestionResults(buildQuestionResults(answered));
            
             return summary;
  }

  private List<QuestionResultDto> buildQuestionResults(List<QAPair> answered) {
         List<QuestionResultDto> results = new ArrayList<>();
         for(QAPair qa : answered){
             results.add(QuestionResultDto.builder().questionNumber(qa.getQuestionNumber()).questionText(qa.getQuestion()).overallScore(qa.getEvaluation().getOverallScore()).confidenceScore(qa.getEvaluation().getConfidenceScore()).build());
         }
         return results;
  }

  private void enforceHistoryLimit(Long userId) {
           List<Interview> oldList =  interviewRepository.findByUserIdAndStatusOrderByCreatedAtAsc(userId,InterviewStatus.COMPLETED.name());
           int excess = oldList.size() - AppConstants.MAX_INTERVIEW_HISTORY;
           if(excess>0){
            List<Interview> toDelete =  oldList.subList(0, excess);
            interviewRepository.deleteAll(toDelete);
            log.info("Enforced interview history limit: deleted {} oldest completed interview(s) for userId={}",
                    excess, userId);
           }         
  }

  private String toJson(List<String> suggestions) {
            try{
              return objectMapper.writeValueAsString(suggestions== null ? new ArrayList<>() : suggestions);
            }
            catch(Exception ex){
              log.error("Failed to serialize improvement suggestions to JSON",ex);
              return null;
            }
  }

  private double averageConfidenceScore(List<QAPair> answered) {
      if(answered.isEmpty()){
        return 0.0;
      }
      double total = 0.0;
      for(QAPair q :answered){
          total+=q.getEvaluation().getConfidenceScore();
      }
      return total;
  }

  private double averageOverall(List<QAPair> answered) {
              if(answered.isEmpty()){
                 return 0.0;
              }
              double total=0.0;
              for(QAPair q :answered){
                  total+= q.getEvaluation().getOverallScore();
              }
              return total;
  }
  
}

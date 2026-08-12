// package com.interviewprep.modules.Interview.serviceImpl;


// import java.util.List;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.interviewprep.exception.BadRequestException;
// import com.interviewprep.exception.ResourceNotFoundException;
// import com.interviewprep.modules.Interview.Repository.InterviewRepository;
// import com.interviewprep.modules.Interview.dto.InterviewAnswerResponseDto;
// import com.interviewprep.modules.Interview.dto.InterviewResponseDto;
// import com.interviewprep.modules.Interview.dto.InterviewStartRequest;
// import com.interviewprep.modules.Interview.dto.InterviewSummaryDto;
// import com.interviewprep.modules.Interview.entity.Difficulty;
// import com.interviewprep.modules.Interview.entity.Interview;
// import com.interviewprep.modules.Interview.entity.InterviewStatus;
// import com.interviewprep.modules.Interview.entity.InterviewType;
// import com.interviewprep.modules.Interview.exception.InterviewException;
// import com.interviewprep.modules.Interview.mapper.InterviewMapper;
// import com.interviewprep.modules.Interview.service.InterviewService;
// import com.interviewprep.modules.ai.dto.ResumeData;
// import com.interviewprep.modules.ai.service.AnswerEvaluationService;
// import com.interviewprep.modules.ai.service.InterviewQuestionService;
// import com.interviewprep.modules.ai.service.SpeechToTextService;
// import com.interviewprep.modules.ai.service.TextToSpeechService;
// import com.interviewprep.modules.interviewsession.model.InterviewSession;
// import com.interviewprep.modules.interviewsession.model.QAPair;
// import com.interviewprep.modules.interviewsession.service.InterviewSessionService;
// import com.interviewprep.modules.profile.entity.UserProfile;
// import com.interviewprep.modules.profile.repository.UserProfileRepository;
// import com.interviewprep.modules.questionbank.entity.HrQuestion;
// import com.interviewprep.modules.questionbank.service.QuestionBankService;
// import com.interviewprep.modules.resume.Repository.ResumeRepository;
// import com.interviewprep.modules.resume.entity.Resume;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @Slf4j
// @Transactional
// @RequiredArgsConstructor
// public class InterviewServiceImpl implements InterviewService {

//       private final InterviewRepository interviewRepository;
//       private final InterviewSessionService interviewSessionService;
//       private final InterviewQuestionService interviewQuestionService;
//       private final AnswerEvaluationService answerEvaluationService;
//       private final SpeechToTextService speechToTextService;
//       private final TextToSpeechService textToSpeechService;
//       private final QuestionBankService questionBankService;
//       private final ResumeRepository resumeRepository;
//       private final UserProfileRepository userProfileRepository;
//       private final InterviewMapper interviewMapper;
//       private final ObjectMapper objectMapper;

//   @Override
//   public InterviewResponseDto startInterview(Long userId, InterviewStartRequest request) {
    
//       UserProfile profile = userProfileRepository.findByUserId(userId).orElseThrow(()-> new BadRequestException("Please complete your profile before starting an interview."));

//         if (interviewRepository.findByUserIdAndStatus(userId, InterviewStatus.IN_PROGRESS.name()).isPresent()) {
//             throw new InterviewException(
//                     "You already have an interview in progress. Please end it before starting a new one.");
//         }
//             String type = normalizeType(request.getInterviewType());
//             String difficulty = normalizeDifficulty(request.getDifficulty());
//             int totalQuestions = request.getTotalQuestions();
//             ResumeData resumeData=null;
//             if(InterviewType.RESUME_BASED.name().equals(type)){
//                resumeData = loadLatestResumeData(userId);
//             }
            
//           Interview interview =  Interview.builder().user(profile.getUser()).interviewType(type).difficulty(difficulty).totalQuestions(totalQuestions).status(InterviewStatus.IN_PROGRESS.name()).build();
//             interview =interviewRepository.save(interview);
//             InterviewSession session =interviewSessionService.createSession(interview.getId(), userId, type, difficulty, totalQuestions, resumeData, request.getCsTopic());
//            String questionText = generateQuestionText(session,true); 
//            int questionNumber =1;
//            session.setCurrentQuestionNumber(questionNumber);
//            session.getHistory().add(QAPair.builder().questionNumber(questionNumber).question(questionText).build());
//            String audioUrl  = synthesizeInto(session,questionText,questionNumber);
//            interviewSessionService.updateSession(session.getSessionId(), session);
//             return InterviewResponseDto.builder().interviewId(interview.getId()).sessionId(session.getSessionId()).interviewType(type).difficulty(difficulty).totalQuestions(totalQuestions).currentQuestionNumber(questionNumber).ttsAudioUrl(audioUrl).build();
//   }

//   private String synthesizeInto(InterviewSession session, String questionText, int questionNumber) {
//          byte[] audio = textToSpeechService.synthesize(questionText);
//           session.setCurrentQuestionAudio(audio);
//           if(audio== null || audio.length==0){
//             return null;
//           }
//           return "/api/interviews/audio"+session.getSessionId()+"/q"+questionNumber;


//   }

//   private String generateQuestionText(InterviewSession session, boolean isFirst) {
                 
//        String type = session.getInterviewType();
//        boolean medium = Difficulty.MEDIUM.name().equals(session.getDifficulty());
//        if(InterviewType.HR.name().equals(type)){
//             if(medium && !isFirst){
//                       return interviewQuestionService.generateQuestion(type,
//                         session.getDifficulty(),null,session.getHistory(),null);
//             }
//             return pickHrQuestion(session);
//        }
//        List<QAPair> context = isFirst ? List.of():session.getHistory();
//        return interviewQuestionService.generateQuestion(type, session.getDifficulty(), session.getCsTopic(), context, session.getResumeData());
//   }
//   private String pickHrQuestion(InterviewSession session) {
//         HrQuestion question = questionBankService.getrandomQuestion(
//                 session.getDifficulty(), session.getAskedQuestionIds());
//         if (question == null && Difficulty.MEDIUM.name().equals(session.getDifficulty())) {
//             // Medium interviews may pull an Easy bank question for the opener (Section 15).
//             question = questionBankService.getrandomQuestion(
//                     Difficulty.EASY.name(), session.getAskedQuestionIds());
//         }
//         if (question == null) {
//             throw new InterviewException("No HR questions are available. Please seed the question bank.");
//         }
//         session.getAskedQuestionIds().add(question.getId());
//         return question.getQuestionText();
//     }


//   private ResumeData loadLatestResumeData(Long userId) {
    
//        Resume resume = resumeRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElseThrow(()->new BadRequestException("please upload a resume first"));
//        String parsed = resume.getParsedData();
//        if(parsed == null || parsed.isBlank()){
//         return null;
//        }
//          try {
//             return objectMapper.readValue(parsed, ResumeData.class);
//         } catch (Exception ex) {
//             log.warn("Could not parse stored resume data for userId={}; proceeding without it", userId, ex);
//             return null;
//         }
//   }

//  private String normalizeDifficulty(String difficulty) {
//         if (difficulty == null) {
//             throw new BadRequestException("difficulty is required.");
//         }
//         String normalized = difficulty.trim().toUpperCase();
//         for (Difficulty d : Difficulty.values()) {
//             if (d.name().equals(normalized)) {
//                 return normalized;
//             }
//         }
//         throw new BadRequestException("Invalid difficulty: " + difficulty);
//     }
// /
//   private String normalizeType(String type) {
//         if(type == null ){
//               throw new BadRequestException("interview type required");
//         }
//         String normalized = type.trim().toUpperCase();
//         for(InterviewType t : InterviewType.values()){
//              if(t.name().equals(normalized)) return normalized;
//         }
//         throw new BadRequestException("Invalid interview Type"+type);
//   }






//   @Override
//   public InterviewAnswerResponseDto processAnswer(Long userId, Long interviewId, byte[] audioData, String filname) {
//               InterviewSession session = interviewSessionService.getByInterviewId(interviewId);
//               if(session ==null){
//                 throw new ResourceNotFoundException("InterviewNotfound");
//               }
              

   
//   }

//   @Override
//   public InterviewSummaryDto endInterview(Long userId, Long interviewId) {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'endInterview'");
//   }
  
// }

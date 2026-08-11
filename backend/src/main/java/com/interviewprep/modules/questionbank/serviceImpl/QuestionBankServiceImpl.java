package com.interviewprep.modules.questionbank.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.interviewprep.modules.questionbank.entity.HrQuestion;
import com.interviewprep.modules.questionbank.repository.HrQuestionRepository;
import com.interviewprep.modules.questionbank.service.QuestionBankService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankServiceImpl implements QuestionBankService{
            private final HrQuestionRepository hrQuestionRepository;
@Override
  public HrQuestion getrandomQuestion(String difficulty, List<Long> excludeIds) {
             List<HrQuestion> all = hrQuestionRepository.findByDifficulty(difficulty);
              if(all == null || all.isEmpty()){
                log.warn("");
                return null;
              }
              Set<Long> exclude = excludeIds == null
        ? new HashSet<>()
        : new HashSet<>(excludeIds);
        List<HrQuestion>available = new ArrayList<>();
        for(HrQuestion q : all){
          if(!exclude.contains(q.getId())){
            available.add(q);
          }
        }

List<HrQuestion> pool = available.isEmpty() ? all : available;
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }


  

  @Override
  public List<HrQuestion> getQuestionsByCategory(String category, String difficulty) {
         
        return hrQuestionRepository.findByCategoryAndDifficulty(category, difficulty);
  }
  
}

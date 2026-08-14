package com.interviewprep.modules.questionbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.questionbank.entity.HrQuestion;
import java.util.List;

@Repository
public interface HrQuestionRepository extends JpaRepository<HrQuestion,Long> {
List<HrQuestion> findByDifficulty(String difficulty);
List<HrQuestion> findByCategoryAndDifficulty(String category,String difficulty);

 long countByDifficulty(String difficulty);
}

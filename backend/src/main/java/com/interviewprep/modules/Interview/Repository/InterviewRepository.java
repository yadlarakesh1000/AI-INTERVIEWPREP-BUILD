package com.interviewprep.modules.Interview.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.Interview.entity.Interview;
@Repository
public interface InterviewRepository extends JpaRepository<Interview,Long> {
      List<Interview> findTop5ByUserIdAndStatusOrderByCreatedAtDesc(Long userId,String status);
     List<Interview> findByUserIdAndStatusOrderByCreatedAtAsc(Long userId,String status);
     Optional<Interview> findByUserIdAndStatus(Long userId,String status);
}

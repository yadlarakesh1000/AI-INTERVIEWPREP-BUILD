package com.interviewprep.modules.skilltracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewprep.modules.skilltracker.entity.DayProgress;

public interface DayProgressRepository  extends JpaRepository<DayProgress,Long>{
  
   List<DayProgress> findByUserRoadmapIdAndWeekNumber(Long userRoadmapId,Integer weekNumber);
   long countByUserRoadmapIdAndIsCompletedTrue(Long userRoadmapId);




}

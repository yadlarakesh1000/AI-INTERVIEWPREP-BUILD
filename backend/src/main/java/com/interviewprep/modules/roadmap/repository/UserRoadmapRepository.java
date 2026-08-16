package com.interviewprep.modules.roadmap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interviewprep.modules.roadmap.entity.UserRoadmap;

public interface UserRoadmapRepository 
            extends JpaRepository<UserRoadmap,Long>{
Optional<UserRoadmap> findByUserIdAndIsActiveTrue(Long userId);
List<UserRoadmap> findByUserIdAndRoadmapTemplateIdOrderByIdAsc(Long userId,Long roadmapTemplateId);
  
}

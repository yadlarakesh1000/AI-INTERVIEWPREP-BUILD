package com.interviewprep.modules.roadmap.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.repository.UserRepository;
import com.interviewprep.modules.roadmap.dto.RoadmapResponseDto;
import com.interviewprep.modules.roadmap.dto.RoadmapSelectionRequest;
import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;
import com.interviewprep.modules.roadmap.entity.UserRoadmap;
import com.interviewprep.modules.roadmap.exception.RoadmapException;
import com.interviewprep.modules.roadmap.mapper.RoadmapMapper;
import com.interviewprep.modules.roadmap.repository.RoadmapTemplateRepository;
import com.interviewprep.modules.roadmap.repository.UserRoadmapRepository;
import com.interviewprep.modules.roadmap.service.RoadmapService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {
       private final RoadmapTemplateRepository roadmapTemplateRepository;
       private final UserRoadmapRepository userRoadmapRepository;
       private final UserRepository userRepository;
       private final ObjectMapper objectMapper;
       private final  RoadmapMapper roadmapMapper;
  @Override
  public List<RoadmapResponseDto> getAllRoadmaps() {
    List<RoadmapResponseDto> responses = new ArrayList<>();
        List<RoadmapTemplate> templates = roadmapTemplateRepository.findAll();
        for(RoadmapTemplate template :templates){
            responses.add(roadmapMapper.toSummary(template)); 
        }
        return responses;
  }

  @Override
  public RoadmapResponseDto getRoadmapById(Long id) {
         RoadmapTemplate template = roadmapTemplateRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("RoadmapTemplate","id",id));
         return toDetail(template,null);
  }

  private RoadmapResponseDto toDetail(RoadmapTemplate template, Integer selectedDurationWeeks) {
        RoadmapResponseDto dto = roadmapMapper.toSummary(template);
        dto.setWeeklyPlan(parseJson(template.getWeeklyPlan()));
        dto.setMilestones(parseJson(template.getMilestones()));
        dto.setRecommendedProjects(parseJson(template.getRecommendedProjects()));
        dto.setLearningResources(parseJson(template.getLearningResources()));
        dto.setSelectedDurationWeeks(selectedDurationWeeks);
        return dto;
    }
  private JsonNode parseJson(String json) {
       if(json == null || json.isBlank()){
         return null;
       }try{
       JsonNode root = objectMapper.readTree(json);
          return root;
       }
       catch(Exception ex){
        log.warn("Failed to parse stored roadmap JSON; returning null", ex);
          return null;
       }


  }

  @Override
  @Transactional
  public RoadmapResponseDto selectRoadmap(Long userId, RoadmapSelectionRequest request) {
     validateDuration(request.getSelectedDurationWeeks());
    
     
   RoadmapTemplate template = roadmapTemplateRepository.findById(request.getRoadmapTemplateId())
        .orElseThrow(() -> new ResourceNotFoundException(
                "RoadmapTemplate", "id", request.getRoadmapTemplateId()));

Optional<UserRoadmap> existingRoadmap =
        userRoadmapRepository.findByUserIdAndIsActiveTrue(userId);

if (existingRoadmap.isPresent()) {
    UserRoadmap existing = existingRoadmap.get();
    existing.setIsActive(false);
    userRoadmapRepository.save(existing);
}
UserRoadmap userRoadmap = userRoadmapRepository.findByUserIdAndRoadmapTemplateIdOrderByIdAsc(userId, template.getId()).stream().findFirst().orElse(null);
if(userRoadmap==null){
   User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
     userRoadmap= UserRoadmap.builder().user(user).roadmapTemplate(template).selectedDurationWeeks(request.getSelectedDurationWeeks()).isActive(true).build();
}
else{
  userRoadmap.setSelectedDurationWeeks(request.getSelectedDurationWeeks());
  userRoadmap.setIsActive(true);

}
userRoadmapRepository.save(userRoadmap);
 log.info("User {} selected roadmap template {} ({} weeks)",
                userId, template.getId(), request.getSelectedDurationWeeks());
     return toDetail(template,request.getSelectedDurationWeeks());
  }
  

  private void validateDuration(Integer selectedDurationWeeks) {
        if (selectedDurationWeeks == null
                || (selectedDurationWeeks != 26 && selectedDurationWeeks != 52)) {
            throw new RoadmapException("Selected duration must be either 26 or 52 weeks.");
        }
    }

  @Override
  @Transactional(readOnly = true)
  public RoadmapResponseDto getMyRoadmap(Long userId) {
            UserRoadmap roadmap = userRoadmapRepository.findByUserIdAndIsActiveTrue(userId).orElseThrow(()->new ResourceNotFoundException("Active roadmap","userId",userId));
            return toDetail(roadmap.getRoadmapTemplate(), roadmap.getSelectedDurationWeeks());
  }
  
}

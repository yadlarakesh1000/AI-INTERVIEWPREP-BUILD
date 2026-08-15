package com.interviewprep.modules.roadmap.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="roadmap_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapTemplate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable=false,length=200)
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
   @Column(name="career_path", nullable=false, length=200) 
  private String careerPath;
 	@Column(name="duration_weeks", nullable=false) 
  private Integer durationWeeks;
  @Column(name="weekly_plan", nullable=false, columnDefinition="JSON")
  private String weeklyPlan;
  @Column(columnDefinition="JSON")
  private String milestones;
  @Column(name="recommended_projects", columnDefinition="JSON")
  private String recommendedProjects;
  @Column(name="learning_resources", columnDefinition="JSON")
  private String learningResources;
  @CreationTimestamp @Column(name="created_at", updatable=false)
  private LocalDateTime createdAt;
}

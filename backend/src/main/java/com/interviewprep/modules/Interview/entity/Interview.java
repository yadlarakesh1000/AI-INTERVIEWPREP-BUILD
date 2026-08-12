package com.interviewprep.modules.Interview.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.interviewprep.modules.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="interviews")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Interview {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       @ManyToOne(fetch =FetchType.LAZY, optional = false)  
       @JoinColumn(name = "user_id", nullable = false)
       private User user;
       @Column(name = "interview_type", nullable = false, columnDefinition = "ENUM('HR','RESUME_BASED','CS_FUNDAMENTALS')")
       private String interviewType;
        @Column(name = "difficulty", nullable = false,columnDefinition = "ENUM('EASY','MEDIUM')")
       private String difficulty;
       @Column(name = "overall_score", precision = 4, scale = 2)
       private BigDecimal overallScore;
       	@Column(name = "confidence_score", precision = 4, scale = 2)
       private BigDecimal confidenceScore;
       @Column(name = "total_questions")
       private Integer totalQuestions;
       @Column(name = "duration_seconds")
       private Integer durationSeconds;
       @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
       private String improvementSuggestions;
       @Column(name = "status", nullable = false, columnDefinition = "ENUM('IN_PROGRESS','COMPLETED','ABANDONED') DEFAULT 'IN_PROGRESS'")
       private String status;
       @CreationTimestamp @Column(name = "created_at", updatable = false)
       private LocalDateTime createdAt;
       @UpdateTimestamp @Column(name = "updated_at")
       private LocalDateTime updatedAt;

}

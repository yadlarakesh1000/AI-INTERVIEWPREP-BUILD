package com.interviewprep.modules.questionbank.entity;

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
@Table(name="hr_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrQuestion {
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    @Column(name = "category", length = 100)
    private String category;
    @Column(name = "difficulty", nullable = false, columnDefinition = "ENUM('EASY','MEDIUM')")
    private String difficulty;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

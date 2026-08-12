package com.interviewprep.modules.Interview.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private String text;
    private int questionNumber;

}

package com.ezen.propick.survey.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name = "survey_questions")
public class SurveyQuestions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", updatable = false, nullable = false)
    private Integer questionId;


    @Column(name ="question_text",nullable = false, length = 255)
    private String questionText;

    @Column(name ="is_optional", nullable = false)
    private Boolean isOptional = true;


}

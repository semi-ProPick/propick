package com.ezen.propick.survey.entity;

import com.ezen.propick.survey.enumpackage.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "question_code")
    private String questionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey surveyId;


    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType = QuestionType.Single;


    @OneToMany(mappedBy = "questionId", fetch = FetchType.LAZY)
    @Builder.Default
    private List<SurveyOptions> options = new ArrayList<>();


}

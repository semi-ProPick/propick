package com.ezen.propick.survey.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="survey_response_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SurveyResponseOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_option_id", updatable = false, nullable = false)
    private Integer responseOptionId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse response;

    @Column(name = "free_text_answer")
    private String freeTextAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private SurveyOptions option;
    @ManyToOne
    @JoinColumn(name="question_id")
    private SurveyQuestions question;

}

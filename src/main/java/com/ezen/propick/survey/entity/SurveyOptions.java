package com.ezen.propick.survey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="survey_options")
public class SurveyOptions {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", updatable = false, nullable = false)
    private Integer optionId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestions questionId;

    @Column(name = "option_code")
    private String optionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_option_id", nullable = true)
    private SurveyOptions parentOption;

    @OneToMany(mappedBy = "parentOption", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SurveyOptions> childOptions = new ArrayList<>();

    @Column(name ="option_text",nullable = false, length = 255)
    private String optionText;



}

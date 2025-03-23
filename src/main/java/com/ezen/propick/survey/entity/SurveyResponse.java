package com.ezen.propick.survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="survey_response")
@EntityListeners(AuditingEntityListener.class)
public class SurveyResponse{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id", updatable = false, nullable = false)
    private Integer responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey surveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestions questionId;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private SurveyOptions optionId;

    // 양방향 1:1 관계 추가
    //cascade = CascadeType.ALL 추가하여 SurveyResponse 삭제 시 Satisfaction도 삭제되도록 설정.
    @OneToOne(mappedBy = "responseId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Satisfaction satisfaction;


    @CreatedDate
    @Column(name ="response_date", nullable = false)
    private LocalDateTime responseDate = LocalDateTime.now();


    @Enumerated(EnumType.STRING)
    @Column(name="response_status", nullable = false)
    private ResponseStatus responseStatus = ResponseStatus.ACTIVE;

    public enum ResponseStatus {
        ACTIVE, DELETED
    }
}

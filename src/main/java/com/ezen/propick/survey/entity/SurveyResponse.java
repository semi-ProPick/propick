package com.ezen.propick.survey.entity;

import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    // 다중 선택 응답 리스트
    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyResponseOption> selectedOptions = new ArrayList<>();


    // 만족도와의 1:1 관계 (응답을 기준으로 만족도 평가)
    @OneToOne(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Satisfaction satisfaction;

    @CreatedDate
    @Column(name ="response_date", nullable = false)
    private LocalDateTime responseDate;


    @Enumerated(EnumType.STRING)
    @Column(name="response_status", nullable = false)
    private ResponseStatus responseStatus = ResponseStatus.ACTIVE;


}

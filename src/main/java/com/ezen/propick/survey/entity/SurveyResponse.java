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


@Entity
@Table(name="survey_response")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SurveyResponse {

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

    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SurveyResponseOption> surveyResponseOptions;
    // 만족도
    @OneToOne(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Satisfaction satisfaction;

    @CreatedDate
    @Column(name ="response_date", nullable = false)
    private LocalDateTime responseDate;

    @Enumerated(EnumType.STRING)
    @Column(name="response_status", nullable = false)
    private ResponseStatus responseStatus = ResponseStatus.ACTIVE;

}

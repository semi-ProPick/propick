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
@Table(name="satisfaction")
@EntityListeners(AuditingEntityListener.class)

public class Satisfaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "satisfaction_id", updatable = false, nullable = false)
    private Integer satisfactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey surveyId;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "response_id", nullable = true)
    private SurveyResponse responseId;

    @Column(name = "satisfaction_score", nullable = false)
    private Integer satisfactionScore;

    public void setSatisfactionScore(Integer score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Satisfaction score must be between 1 and 5");
        }
        this.satisfactionScore = score;
    }

    @CreatedDate
    @Column(name = "satisfaction_created_at", updatable = false)
    private LocalDateTime satisfactionCreatedAt = LocalDateTime.now();
}



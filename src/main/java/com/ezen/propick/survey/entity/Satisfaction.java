package com.ezen.propick.survey.entity;

import com.ezen.propick.user.entity.User;
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
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey surveyId;

    // 응답과 반드시 연결되도록 설정
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse response;


    @Column(name = "satisfaction_score", nullable = false)
    private Integer satisfactionScore;

    public void setSatisfactionScore(Integer score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("만족도는 1-5만~");
        }
        this.satisfactionScore = score;
    }

    @CreatedDate
    @Column(name = "satisfaction_created_at", updatable = false)
    private LocalDateTime satisfactionCreatedAt;
}



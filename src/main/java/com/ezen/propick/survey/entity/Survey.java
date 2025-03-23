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
@Table(name = "survey")
@EntityListeners(AuditingEntityListener.class)
/*@CreatedDate는 Spring Data JPA의 Auditing 기능에 의존하므로, 엔티티가 Auditing을 사용할 수 있도록*/
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id", updatable = false, nullable = false)
    private Integer surveyId;

    @Column(name ="survey_title",nullable = false, length = 100)
    private String surveyTitle;

    @CreatedDate //생성 시점에 자동으로 날짜 생성
    @Column(name ="survey_created_at", nullable = false)
    private LocalDateTime surveyCreatedAt = LocalDateTime.now();

    @Column(name = "survey_updated_at")
    private LocalDateTime surveyUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status surveyStatus = Status.DRAFT;

    @PreUpdate
    public void preUpdate() {
        this.surveyUpdatedAt = LocalDateTime.now();
    }

    public enum Status {
        DRAFT, COMPLETED,DELETED
    }

}

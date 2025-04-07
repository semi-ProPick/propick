package com.ezen.propick.survey.dto.recommendation;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SatisfactionDTO {


    private Integer satisfactionId;
    @NotNull
    private Integer surveyId;

    @NotNull
    private Integer responseId;

    @NotNull(message = "만족도 점수는 필수입니다.")
    @Min(value = 0, message = "0점 이상이어야 합니다.")
    @Max(value = 5, message = "5점 이하여야 합니다.")
    private Integer satisfactionScore;

    @CreatedDate
    @Column(name ="satisfaction_created_at", nullable = false)
    private LocalDateTime satisfactionCreatedAt;
}

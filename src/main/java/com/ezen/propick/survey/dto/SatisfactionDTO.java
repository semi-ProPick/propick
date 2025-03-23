package com.ezen.propick.survey.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder

public class SatisfactionDTO {

    private Integer satisfactionId;
    private Integer userNo;
    private Integer surveyId;
    private Integer responseId;

    @NotNull(message = "만족도 점수는 필수입니다.")
    @Min(value = 1, message = "만족도 점수는 1 이상이어야 합니다.")
    @Max(value = 5, message = "만족도 점수는 5 이하여야 합니다.")
    private Integer satisfactionScore;
    private LocalDateTime satisfactionCreatedAt;
}

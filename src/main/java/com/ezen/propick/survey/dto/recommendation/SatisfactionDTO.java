package com.ezen.propick.survey.dto.recommendation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SatisfactionDTO {

    @NotNull
    private Integer satisfactionId;
    @NotNull
    private Integer surveyId;

    @NotNull
    private Integer responseId;

    @NotNull(message = "만족도 점수는 필수입니다.")
    @Min(value = 1, message = "1점 이상이어야 합니다.")
    @Max(value = 5, message = "5점 이하여야 합니다.")
    private Integer satisfactionScore;
    private LocalDateTime satisfactionCreatedAt;
}

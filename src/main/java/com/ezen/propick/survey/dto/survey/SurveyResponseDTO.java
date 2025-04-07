package com.ezen.propick.survey.dto.survey;

import com.ezen.propick.survey.entity.SurveyResponseOption;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder

public class SurveyResponseDTO {
    private Integer responseId;
    private Integer surveyId;
    private Integer userNo;
    private Integer questionId;


    //@PositiveOrZero(message = "선택지 ID는 0 이상이어야 합니다.")
    private Integer optionId;

    private List<Integer> selectedOptions; // 다중 선택을 고려하여 리스트로 변경

    @Builder.Default
    private LocalDateTime responseDate = LocalDateTime.now();

    @NotNull(message = "응답 상태는 필수입니다.")
    @Pattern(regexp = "Active|Deleted", message = "응답 상태는 'Active' 또는 'Deleted'이어야 합니다.")
    private String responseStatus;



}

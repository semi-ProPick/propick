package com.ezen.propick.survey.dto.survey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder

public class SurveyDTO {

    private Integer surveyId;

    @NotBlank(message = "설문지 제목을 입력해주세요.")
    @Size(min = 1, max = 100, message = "설문지 제목은 1~100자 이내여야 합니다.")
    private String surveyTitle;

    private List<SurveyQuestionDTO> questions; // 질문 리스트 포함

    @Builder.Default
    private LocalDateTime surveyCreatedAt = LocalDateTime.now();

    private LocalDateTime surveyUpdatedAt;

    @NotBlank(message = "상태는 필수입니다.")
    @Pattern(regexp = "Draft|Completed|Deleted", message = "상태는 'Draft', 'Completed', 'Deleted' 중 하나여야 합니다.")
    private String surveyStatus;

}

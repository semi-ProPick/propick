package com.ezen.propick.survey.dto.survey;

import com.ezen.propick.survey.enumpackage.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder

public class SurveyQuestionDTO {

    private Integer questionId;

    @NotBlank(message = "질문 내용은 필수입니다.")
    @Size(min = 1, max = 255, message = "질문 내용은 1~255자 이내여야 합니다.")
    private String questionText;

    private QuestionType questionType;

    private String questionCode;

    private List<SurveyOptionsDTO> options;

    @NotNull(message = "선택 여부는 필수입니다.")
    private Boolean isOptional;
}

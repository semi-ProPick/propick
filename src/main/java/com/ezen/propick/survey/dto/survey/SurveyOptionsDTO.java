package com.ezen.propick.survey.dto.survey;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder

public class SurveyOptionsDTO {

    private Integer optionId;

    private Integer questionId;

    @NotBlank(message = "선택지 내용은 필수입니다.")
    @Size(min = 1, max = 255, message = "선택지 내용은 1~255자 이내여야 합니다.")
    private String optionText;

    //parentOptionId가 null이어도 되는 경우 유효성 검사를 제거하는 것이 맞음
    //@PositiveOrZero(message = "상위 선택지 ID는 0 이상이어야 합니다.")
    private Integer parentOptionId;//NULL 허용해야함!!

    private List<SurveyOptionsDTO> childOptions;
}

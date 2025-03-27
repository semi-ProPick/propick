package com.ezen.propick.survey.dto.survey;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseRequestDTO {
    private Integer surveyId; // 설문 ID
    private List<AnswerDTO> answers; // 사용자의 응답 리스트
}

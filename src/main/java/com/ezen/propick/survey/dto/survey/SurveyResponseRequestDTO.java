package com.ezen.propick.survey.dto.survey;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SurveyResponseRequestDTO {
    private Integer surveyId;
    private Integer userNo;
    private List<AnswerDTO> responses;

    @Setter
    @Getter
    public static class AnswerDTO {
        private Integer questionId;
        private Integer optionId;
    }
}
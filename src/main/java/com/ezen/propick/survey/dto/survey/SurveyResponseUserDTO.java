package com.ezen.propick.survey.dto.survey;

import com.ezen.propick.survey.entity.SurveyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseUserDTO {
    private Integer responseId;
    private String surveyTitle;
    private String responseDate;

    public static SurveyResponseUserDTO fromEntity(SurveyResponse entity) {
        return new SurveyResponseUserDTO(
                entity.getResponseId(),
                entity.getSurveyId().getSurveyTitle(),
                entity.getResponseDate().toLocalDate().toString()
        );
    }
}
package com.ezen.propick.survey.dto.survey;

import com.ezen.propick.survey.entity.SurveyOptions;
import com.ezen.propick.survey.entity.SurveyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SurveyResponseOptionDTO {

    private Integer responseOptionId;

    private SurveyResponse responseId;

    private SurveyOptions optionId;
}

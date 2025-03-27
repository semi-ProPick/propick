package com.ezen.propick.survey.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SurveyRecommendationResultDTO {
    private double bmi;
    private String bmiStatus;
    private double minIntakeGram;
    private double maxIntakeGram;
    private List<String> recommendedTypes;
    private List<String> avoidTypes;
    private String intakeTiming;
    private List<String> warningMessages;
}

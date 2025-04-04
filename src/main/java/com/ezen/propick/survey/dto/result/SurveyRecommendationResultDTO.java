package com.ezen.propick.survey.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
/*
* ProteinRecommendationEngine이 분석을 완료한 후, 최종적인 분석 결과를 담는 DT
* 설문 분석 API (/api/survey-analysis/{responseId})의 반환값으로 제공
* 프론트엔드에서 사용자가 결과 페이지를 볼 때 직접 표시될 내용들입니다.
* BMI, 추천 단백질 유형, 섭취량, 타이밍, 건강 주의사항 등을 시각화하기 위해 사용됩니다.
* */
public class SurveyRecommendationResultDTO {
    private String name;
    private String gender;
    private int age;
    private double bmi;
    private String bmiStatus;
    private double minIntakeGram;
    private double maxIntakeGram;
    private List<String> recommendedTypes;
    private List<String> avoidTypes;
    private String intakeTiming;
    private List<String> warningMessages;
    private Map<String, Integer> healthConcerns;
    Map<String, Integer> proteinRecommendationStats;
    private Map<String, Integer> intakeTimingRatio;

}

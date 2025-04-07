package com.ezen.propick.survey.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
/* 분석 로직 결과를 담은 중간 결과
 * 컨트롤러(RecommendationController)가 프론트로 반환하는 구조.
 */
@Getter
@Builder
@AllArgsConstructor
public class SurveyRecommendationResultDTO {

    private String name;
    private String gender;
    private int age;

    // BMI 정보
    private double bmi;
    private String bmiStatus;

    // 단백질 섭취량 (최소/최대)
    private double minIntakeGram;
    private double maxIntakeGram;

    // 단백질 추천/회피 유형
    private List<String> recommendedTypes;
    private List<String> avoidTypes;

    // 섭취 타이밍
    private String intakeTiming;

    // 주의 메시지
    private List<String> warningMessages;

    // 🔥 추가 정보들: 모두 저장용으로 JSON 변환 가능
    private Map<String, Integer> healthConcerns;               // 건강 고민
    private Map<String, Integer> proteinRecommendationStats;   // 단백질 추천 통계
    private Map<String, Integer> timingRatio;                  // 섭취 시간대 비율
}
package com.ezen.propick.survey.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
/*
* 최종 추천 제품의 구체적인 정보를 담는 DTO
* 추천 결과 조회 API (/api/recommendations/{surveyResponseId})의 반환값
* 사용자가 추천받은 제품의 이름, 섭취 권장량, 타이밍, 단백질 유형, 주의사항 등을 최종적으로 확인할 때 사용됩니다.
* 마이페이지나 결과 페이지에서 추천 제품을 구체적으로 확인할 때 필요합니다.
* */
public class RecommendationResponseDTO {
    private Integer recommendationId;

    private double bmi;
    private String bmiStatus;

    private double minIntakeGram;
    private double maxIntakeGram;

    private List<String> recommendedTypes;
    private List<String> avoidTypes;

    private String intakeTiming;
    private List<String> warningMessages;

    private String productName;

    // 추가 권장 필드 (옵션)
    private String gender;
    private int age;

    private Map<String, Integer> healthConditions; // 건강 상태 가중치 (질병명과 점수)
    Map<String, Integer> recommendedTypeScores;
}
package com.ezen.propick.survey.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Map;

/* 분석 결과를 사용자에게 전달하는 최종 DTO
 * 컨트롤러(RecommendationController)가 프론트로 반환하는 구조.
 */

@Getter
@AllArgsConstructor
@Builder
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

    private String gender;
    private int age;
    private String name;

    // ✅ 건강 상태, 단백질 유형 비중, 섭취 타이밍 비율 (Chart.js 시각화용)
    private Map<String, Integer> healthConditions;
    private Map<String, Integer> recommendedTypeScores;
    private Map<String, Integer> intakeTimingRatio;
}

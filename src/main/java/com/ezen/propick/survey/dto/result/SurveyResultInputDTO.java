package com.ezen.propick.survey.dto.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
/*사용자의 설문 응답 데이터를 최종 분석용으로 정리한 DTO
 * 설문 분석 API (/api/survey-analysis/{responseId})의 반환값으로 제공됩니다.
 * 프론트엔드에서 사용자가 결과 페이지를 볼 때 직접 표시될 내용들입니다.
 * BMI, 추천 단백질 유형, 섭취량, 타이밍, 건강 주의사항 등을 시각화하기 위해 사용됩니다.
 */
public class SurveyResultInputDTO {
    private double heightCm;
    private double weightKg;
    private String purpose; // ex. "근육 증가"
    private String workoutFreq; // ex. "주2~3회"
    private Map<String, Integer> healthConcerns; // ex. ["간 질환", "수면장애"]
    private int age;
    private String gender;
    private String name;
}

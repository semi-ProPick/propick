package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ProteinRecommendationEngineDebugTest {
    private final ProteinRecommendationEngine engine = new ProteinRecommendationEngine();

    @Test
    void debugRecommendationResult() {
        //  원하는 조건 입력
        SurveyResultInputDTO input = SurveyResultInputDTO.builder()
                .heightCm(195)
                .weightKg(70)
                .purpose("식사대용")
                .workoutFreq("주2~3회")
                .healthConcerns(List.of("간 질환", "부종"))
                .build();

        //  알고리즘 실행
        SurveyRecommendationResultDTO result = engine.generate(input);

        //  결과 콘솔 출력
        System.out.println("==== 추천 결과 ====");
        System.out.println("BMI: " + result.getBmi() + " (" + result.getBmiStatus() + ")");
        System.out.println("섭취량: " + result.getMinIntakeGram() + "g ~ " + result.getMaxIntakeGram() + "g");
        System.out.println("추천 단백질: " + result.getRecommendedTypes());
        System.out.println("회피 단백질: " + result.getAvoidTypes());
        System.out.println("섭취 타이밍: " + result.getIntakeTiming());
        System.out.println("경고 메시지:");
        result.getWarningMessages().forEach(msg -> System.out.println(" - " + msg));
    }
}

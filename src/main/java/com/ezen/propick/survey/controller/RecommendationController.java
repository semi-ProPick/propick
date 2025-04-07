package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.result.RecommendationResponseDTO;
import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.entity.Recommendation;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.service.SurveyAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;
    private final SurveyAnalysisService surveyAnalysisService;

    /**
     * 특정 설문 응답 ID에 기반한 단백질 추천 결과를 조회하는 API
     *
     * @param surveyResponseId 설문 응답의 ID
     * @return 추천 결과 데이터를 담은 DTO (JSON 형식)
     */
    @GetMapping("/{surveyResponseId}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(@PathVariable Integer surveyResponseId) {
        Recommendation recommendation = recommendationRepository.findByResponseId_ResponseId(surveyResponseId)
                .orElseThrow(() -> new IllegalArgumentException("추천 정보를 찾을 수 없습니다."));

        // 설문 분석 결과를 받아오기
        SurveyRecommendationResultDTO analysisDto = surveyAnalysisService.analyzeSurvey(surveyResponseId);

        // 건강 상태별 가중치 계산 (변수로 저장하여 중복 호출 방지)
        Map<String, Integer> healthScores = calculateHealthConditionScores(analysisDto.getHealthConcerns());

        // 단백질 비중 정보 가져오기 (오류 해결)
        Map<String, Integer> proteinMap = analysisDto.getProteinRecommendationStats();

        // 최종 RecommendationResponseDTO 구성
        RecommendationResponseDTO resultDto = new RecommendationResponseDTO(
                recommendation.getRecommendationId(),
                analysisDto.getBmi(),
                analysisDto.getBmiStatus(),
                analysisDto.getMinIntakeGram(),
                analysisDto.getMaxIntakeGram(),
                analysisDto.getRecommendedTypes(),
                analysisDto.getAvoidTypes(),
                analysisDto.getIntakeTiming(),
                analysisDto.getWarningMessages(),
                recommendation.getProductId().getProductName(),
                analysisDto.getGender(),
                analysisDto.getAge(),
                healthScores,
                proteinMap
        );

        return ResponseEntity.ok(resultDto);
    }

    // 건강 상태 가중치 계산 메서드
    private Map<String, Integer> calculateHealthConditionScores(Map<String, Integer> concernCountMap) {
        Map<String, Integer> scores = new HashMap<>();

        for (Map.Entry<String, Integer> entry : concernCountMap.entrySet()) {
            String concern = entry.getKey();
            int count = entry.getValue(); // 몇 번 선택되었는지
            int baseScore = determineScore(concern); // 우선순위 점수
            int adjustedScore = baseScore + (count * 5); // 선택 개수마다 +5점 보정

            // 최대 100점 제한
            scores.put(concern, Math.min(adjustedScore, 100));
        }

        return scores;
    }

    // 점수 결정 메서드
    private int determineScore(String concern) {
        return switch (concern) {
            case "신장 질환", "간 질환", "심혈관 질환" -> 90;
            case "피로", "소화불량", "부종", "관절염" -> 70;
            default -> 50;
        };
    }

}

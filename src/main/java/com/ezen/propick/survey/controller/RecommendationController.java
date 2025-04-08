package com.ezen.propick.survey.controller;

import com.ezen.propick.auth.model.AuthDetails;
import com.ezen.propick.survey.dto.result.RecommendationResponseDTO;
import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import com.ezen.propick.survey.entity.Recommendation;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.service.SurveyResponseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;
    private final SurveyResponseService surveyResponseService;
    private final ProteinRecommendationEngine recommendationEngine;

    @GetMapping("/{surveyResponseId}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(
            @PathVariable Integer surveyResponseId,
            @AuthenticationPrincipal AuthDetails userDetails
    ) {
        String userId = userDetails.getUserId();

        // (1) 해당 사용자의 응답 기반 Recommendation이 존재하는지 확인
        Recommendation recommendation = recommendationRepository
                .findByResponseId_ResponseIdAndUser_UserId(surveyResponseId, userId)
                .orElseThrow(() -> new IllegalArgumentException("추천 정보를 찾을 수 없거나 권한이 없습니다."));

        // (2) 설문 응답 → 분석 입력 DTO 생성
        SurveyResultInputDTO inputDto = surveyResponseService.getSurveyResultInputDTO(surveyResponseId);

        // (3) 분석 실행
        SurveyRecommendationResultDTO result = recommendationEngine.generate(inputDto);
        System.out.println(" 분석 결과 확인:");
        System.out.println("BMI: " + result.getBmi() + ", 상태: " + result.getBmiStatus());
        System.out.println("건강 고민: " + result.getHealthConcerns());
        System.out.println("단백질 통계: " + result.getProteinRecommendationStats());
        System.out.println("섭취 타이밍: " + result.getTimingRatio());
        System.out.println("추천 단백질: " + result.getRecommendedTypes());
        System.out.println("회피 단백질: " + result.getAvoidTypes());
        System.out.println("경고 메시지: " + result.getWarningMessages());
        // (4) 프론트로 전달할 결과 DTO 구성
        RecommendationResponseDTO response = new RecommendationResponseDTO(
                recommendation.getRecommendationId(), // ID는 그대로 전달
                result.getBmi(),
                result.getBmiStatus(),
                result.getMinIntakeGram(),
                result.getMaxIntakeGram(),
                result.getRecommendedTypes(),
                result.getAvoidTypes(),
                result.getIntakeTiming(),
                result.getWarningMessages(),
                (recommendation.getProductId() != null ? recommendation.getProductId().getProductName() : null),
                result.getGender(),
                result.getAge(),
                result.getName(),
                result.getHealthConcerns(),
                result.getProteinRecommendationStats(),
                result.getTimingRatio()
        );

        return ResponseEntity.ok(response);
    }
}

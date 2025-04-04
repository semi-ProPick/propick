package com.ezen.propick.survey.service;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import com.ezen.propick.survey.entity.Recommendation;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ProteinType;
import com.ezen.propick.survey.enumpackage.RecommendationTiming;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProteinRecommendationEngine recommendationEngine;
    private final RecommendationRepository recommendationRepository;
    private final SurveyResponseRepository responseRepository;
    private final ProductRepository productRepository; // 제품 추천 로직이 있을 경우 사용

    /**
     * 설문 응답 ID와 입력 DTO를 바탕으로 분석 결과 생성 → 추천 정보 저장
     * @param surveyResponseId 설문 응답 ID
     * @param inputDto         분석을 위한 입력 데이터
     * @param productId        추천 제품 ID
     * @param userId           로그인한 사용자 ID
     * @return Recommendation 저장된 엔티티
     */

    public Recommendation createAndSaveRecommendation(
            Integer surveyResponseId,
            SurveyResultInputDTO inputDto,
            Integer productId,
            String userId // ✅ 문자열 기반 로그인 ID
    ) {
        // 1. 분석 결과 생성
        SurveyRecommendationResultDTO result = recommendationEngine.generate(inputDto);

        // 2. 설문 응답 엔티티 조회
        SurveyResponse response = responseRepository.findById(surveyResponseId)
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));


        // 3. 추천할 제품 조회 ***추후 제품 추천 로직 으로 바꾸기
        Product recommendedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("추천할 제품을 찾을 수 없습니다."));

        // Recommendation 저장
        Recommendation recommendation = Recommendation.builder()
                .responseId(response)
                .user(response.getUser())
                .productId(recommendedProduct)
                .recommendationIntakeAmount(BigDecimal.valueOf(result.getMinIntakeGram()))
                .recommendationWarning(String.join(";", result.getWarningMessages()))
                .build();


        // DB에 추천 결과 저장
        return recommendationRepository.save(recommendation);
    }

    /**
     * 추천 단백질 String → ProteinType Enum 변환
     */
    private ProteinType convertToProteinType(String type) {
        try {
            return ProteinType.valueOf(type);
        } catch (Exception e) {
            throw new IllegalArgumentException("❌ 유효하지 않은 단백질 타입: " + type);
        }
    }

    /**
     * 섭취 타이밍 String → RecommendationTiming Enum 변환
     */
    private RecommendationTiming convertToTiming(String timing) {
        try {
            return RecommendationTiming.valueOf(timing);
        } catch (Exception e) {
            throw new IllegalArgumentException("❌ 유효하지 않은 섭취 타이밍: " + timing);
        }
    }
}
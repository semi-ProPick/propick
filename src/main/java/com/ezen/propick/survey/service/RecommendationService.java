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

    public Recommendation createAndSaveRecommendation(
            Integer surveyResponseId,
            SurveyResultInputDTO inputDto,
            Integer productId,
            String userId // ✅ 문자열 기반 로그인 ID
    ) {
        // 추천 로직 엔진으로부터 결과 DTO 생성
        SurveyRecommendationResultDTO result = recommendationEngine.generate(inputDto);

        // 연관된 SurveyResponse 엔티티 가져오기
        SurveyResponse response = responseRepository.findById(surveyResponseId)
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));

        // 추천할 제품 정보 (제품 추천 로직이 있다면 활용)
        Product recommendedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("추천할 제품을 찾을 수 없습니다."));

        // Recommendation 저장
        Recommendation recommendation = Recommendation.builder()
                .responseId(response)
                .user(response.getUser())// ✅ User 엔티티 자체를 넣음
                .productId(recommendedProduct)
                .recommendationIntakeAmount(BigDecimal.valueOf(result.getMinIntakeGram()))
                .recommendationProteinType(ProteinType.valueOf(result.getRecommendedTypes().get(0)))
                .recommendationTiming(RecommendationTiming.valueOf(result.getIntakeTiming()))
                .recommendationWarning(String.join(";", result.getWarningMessages()))
                .build();

        // DB에 추천 결과 저장
        recommendationRepository.save(recommendation);

        return recommendation;
    }
}
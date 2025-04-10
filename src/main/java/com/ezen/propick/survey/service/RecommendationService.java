package com.ezen.propick.survey.service;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import com.ezen.propick.survey.entity.Recommendation;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ProteinType;
import com.ezen.propick.survey.enumpackage.RecommendationTiming;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SurveyResponseRepository responseRepository;
    private final ProductRepository productRepository;
    private final MainProductService mainProductService;
    private final BookmarkService bookmarkService;
    /**
     * ✅ 최소 정보만 저장하는 추천 저장 로직
     * @param surveyResponseId 설문 응답 ID
     * @param productId 추천 제품 ID (nullable 허용)
     * @param userId 로그인한 사용자 ID
     */

    public Recommendation createAndSaveRecommendation(
            Integer surveyResponseId,
            Integer productId,
            String userId
    ) {
        // 설문 응답 조회
        SurveyResponse response = responseRepository.findById(surveyResponseId)
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));

        // 2. 제품 조회 (nullable 허용)
        Product recommendedProduct = null;
        if (productId != null) {
            recommendedProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("추천할 제품을 찾을 수 없습니다."));
        }

        // 3. Recommendation 저장
        Recommendation recommendation = Recommendation.builder()
                .responseId(response)
                .user(response.getUser())
                .productId(recommendedProduct)
                .build();


        return recommendationRepository.save(recommendation);
    }
    public List<ProductListDTO> findProductsByRecommendedTypes(List<String> recommendedTypes, String userId) {
        List<Product> allProducts = productRepository.findAll();

        List<ProductListDTO> recommended = allProducts.stream()
                .filter(p -> recommendedTypes.contains(p.getProductType()))
                .map(mainProductService::convertToProductListDTO)
                .collect(Collectors.toList());

        return bookmarkService.getProductsWithBookmarkStatus(userId, recommended);
    }

    /**
     * 추천 단백질 String → ProteinType Enum 변환
     */
    private ProteinType convertToProteinType(String type) {
        try {
            return ProteinType.valueOf(type);
        } catch (Exception e) {
            return ProteinType.WPI; // 기본값
        }
    }

    /**
     * 섭취 타이밍 String → RecommendationTiming Enum 변환
     */
    private RecommendationTiming convertToTiming(String timingText) {
        if (timingText == null) return RecommendationTiming.아침;

        if (timingText.contains("운동")) return RecommendationTiming.운동후;
        if (timingText.contains("취침")) return RecommendationTiming.취침전;
        if (timingText.contains("점심")) return RecommendationTiming.점심;
        if (timingText.contains("저녁")) return RecommendationTiming.저녁;
        if (timingText.contains("아침")) return RecommendationTiming.아침;

        return RecommendationTiming.아침;
    }

    // ✅ 건강 상태 점수 계산 로직 (optionCode → 카테고리 기반)
    private static final Map<String, Integer> HEALTH_DETAIL_COUNT = Map.of(
            "소화 장", 4,
            "피부 질환", 5,
            "신장 부담", 4,
            "수면 장애", 3,
            "관절 건강", 2,
            "간 건강", 2,
            "혈관 건강", 3
    );

    public Map<String, Integer> calculateHealthConditionScores(Map<String, Integer> concernMap) {
        Map<String, Integer> result = new LinkedHashMap<>();
        Map<String, Integer> selectedCountByCategory = new LinkedHashMap<>();

        // [1] 각 하위 증상 → 상위 카테고리 매핑
        for (Map.Entry<String, Integer> entry : concernMap.entrySet()) {
            String childCode = entry.getKey();
            String parentLabel = getParentLabelByOptionCode(childCode);

            if (parentLabel != null) {
                selectedCountByCategory.put(
                        parentLabel,
                        selectedCountByCategory.getOrDefault(parentLabel, 0) + entry.getValue()
                );
            }
        }
        // [2] 실제 점수 계산 (항목당 비중 × 선택 수)
        for (Map.Entry<String, Integer> entry : selectedCountByCategory.entrySet()) {
            String category = entry.getKey();
            int selected = entry.getValue();
            int total = HEALTH_DETAIL_COUNT.getOrDefault(category, 1);
            double score = ((double) selected / total) * 100.0;
            result.put(category, Math.min((int) Math.round(score), 100)); // 100점 cap
        }

        // [3] 선택 안 한 항목은 0점으로 고정
        for (String category : HEALTH_DETAIL_COUNT.keySet()) {
            result.putIfAbsent(category, 0);
        }

        return result;
    }


    private String getParentLabelByOptionCode(String code) {
        return switch (code) {
            case "CONSTIPATION", "DIARRHEA", "LACTOSE" -> "소화 장";
            case "ACNE", "ALLERGY" -> "피부 질환";
            case "KIDNEY" -> "신장 부담";
            case "SLEEP" -> "수면 장애";
            case "ARTHRITIS" -> "관절 건강";
            case "LIVER", "TIRED" -> "간 건강";
            case "CARDIO" -> "혈관 건강";
            default -> null;
        };
    }
}

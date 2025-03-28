package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.result.RecommendationResponseDTO;
import com.ezen.propick.survey.entity.Recommendation;
import com.ezen.propick.survey.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
//(추천 결과 조회 API)
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;


    /**
     * 특정 설문 응답 ID에 기반한 단백질 추천 결과를 조회하는 API
     *
     * @param surveyResponseId 설문 응답의 ID
     * @return 추천 결과 데이터를 담은 DTO (JSON 형식)
     */

    @GetMapping("/{surveyResponseId}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendation(@PathVariable Integer surveyResponseId) {
        // RecommendationRepository를 이용해 설문 응답 ID로 추천 결과를 조회
        Recommendation recommendation = recommendationRepository.findByResponseId_ResponseId(surveyResponseId)
                .orElseThrow(() -> new IllegalArgumentException("추천 정보를 찾을 수 없습니다."));

        // 엔티티(Recommendation)를 DTO(RecommendationResponseDTO)로 변환하여 API 응답 준비
        RecommendationResponseDTO resultDto = new RecommendationResponseDTO(
                recommendation.getRecommendationId(),  // 추천 결과의 ID
                recommendation.getRecommendationIntakeAmount().doubleValue(),  // 하루 섭취 권장량 (double로 변환)
                recommendation.getRecommendationTiming().name(),  // 섭취하기 가장 좋은 타이밍 (Enum을 문자열로 변환)
                recommendation.getRecommendationProteinType().name(),  // 추천 단백질 종류 (Enum을 문자열로 변환)
                Arrays.asList(recommendation.getRecommendationWarning().split(";")),  // 주의사항 (DB에 세미콜론(;)으로 구분된 문자열 → 리스트 변환)
                recommendation.getProductId().getProductName()  // 추천하는 제품의 이름
        );

        return ResponseEntity.ok(resultDto);
    }
}
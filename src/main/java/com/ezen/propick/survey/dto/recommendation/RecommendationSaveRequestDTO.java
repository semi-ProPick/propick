package com.ezen.propick.survey.dto.recommendation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
//저장 (요청)
public class RecommendationSaveRequestDTO {
    private Integer responseId;  // 설문 응답 ID
    private Integer productId;   // 추천된 제품 ID
    private Integer userNo;      // 사용자 번호
    private BigDecimal intakeAmount; // 섭취량 (g)

    private String timing;       // PostWorkout / BeforeBed / Morning
    private String proteinType;  // WPI / WPH / WPC / ISP / Casein
    private String warning;      // 경고 메시지 (없으면 null)
}

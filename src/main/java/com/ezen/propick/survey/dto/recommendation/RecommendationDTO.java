package com.ezen.propick.survey.dto.recommendation;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
// 조회/응답(응답) - 엔티티 정보를 모두 담아 클라이언트에 전달
public class RecommendationDTO {
    private Integer recommendationId;
    private Integer responseId;
    private Integer productId;
    private Integer userNo;

    @NotNull(message = "섭취량 안내는 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "섭취량은 0 이상이어야 합니다.")
    private BigDecimal recommendationIntakeAmount;

    @NotNull(message = "섭취 시기 안내는 필수입니다.")
    @Pattern(regexp = "PostWorkout|BeforeBed|Morning", message = "섭취 시기는 'PostWorkout', 'BeforeBed', 'Morning' 중 선택 되어야 합니다.")
    private String recommendationTiming;

    @NotNull(message = "단백질 유형 안내은 필수입니다.")
    @Pattern(regexp = "WPC|WPI|WPH|ISP|Casein", message = "단백질 유형은 'WPC', 'WPI', 'WPH', 'ISP', 'Casein' 중 선택 되어야 합니다.")
    private String recommendationProteinType;

    @Size(max = 1000, message = "경고 메시지는 1000자를 초과할 수 없습니다.")
    private String recommendationWarning;
}

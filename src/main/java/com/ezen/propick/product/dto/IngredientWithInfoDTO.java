package com.ezen.propick.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientWithInfoDTO {
    // 성분 등록, 조회용

    private Integer ingredientId;
    private String ingredientName; // 성분명
    private BigDecimal ingredientAmount; // 성분량
    private String ingredientUnit;  // 성분단위



    // 성분량 + 단위
    public String getIngredientInfo() {
        if (ingredientAmount == null || ingredientUnit == null) {
            return "정보 없음";
        }
        return ingredientAmount.stripTrailingZeros().toPlainString() + " " + ingredientUnit;
    }

    // 성분량을 100g 기준으로 계산하는 메서드
    public BigDecimal calculatePer100g(Integer servingSize) {
        if (servingSize == null || servingSize <= 0 || ingredientAmount == null || ingredientAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return ingredientAmount.divide(BigDecimal.valueOf(servingSize), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }


}

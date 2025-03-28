package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientWithInfoDTO {
    private String ingredientName;
    private BigDecimal ingredientAmount;
    private String ingredientUnit;

    // 새 필드: ingredientAmount와 ingredientUnit 결합
    private String ingredientInfo;

    public IngredientWithInfoDTO(String ingredientName, BigDecimal ingredientAmount, String ingredientUnit) {
        this.ingredientName = ingredientName;
        this.ingredientAmount = ingredientAmount;
        this.ingredientUnit = ingredientUnit;
        this.ingredientInfo = ingredientAmount.stripTrailingZeros().toPlainString() + " " + ingredientUnit; // 결합된 값
    }
}

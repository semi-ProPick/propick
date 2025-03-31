package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDTO {

    private Integer ingredientId;   // 성분 아이디
    private String ingredientName;  // 성분명
    private Integer proteinAmount;  // 단백질 함량
    private Integer servingSize;  // 1회 섭취량
    private Integer calories;  // 칼로리, 에너지

}

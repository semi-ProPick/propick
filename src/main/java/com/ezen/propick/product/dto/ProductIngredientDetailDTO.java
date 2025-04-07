package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredientDetailDTO {
    private String ingredientName; // 성분 이름
    private BigDecimal ingredientAmount; // 성분 양
    private String ingredientUnit; // 성분 단위
}

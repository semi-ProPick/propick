package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.Ingredient;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductIngredientDTO {
    // 상품 등록 시 성분 dto 용(이거 안씀) 겹쳐서

    private Integer ingredientId;   // 성분 아이디
    private String ingredientName;  // 성분명
    private BigDecimal ingredientAmount;  // 성분량
    private String ingredientUnit; // 성분 단위 (예: g, mg, ml 등)
    //private Ingredient ingredient; // 성분에 대한 추가 정보

}

package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductIngredientDTO {

    private Integer ingredientId;   // 성분 아이디
//    private String ingredientName;  // 성분명
    private Integer ingredientAmount;  // 단백질 함량
    private String ingredientUnit;

}

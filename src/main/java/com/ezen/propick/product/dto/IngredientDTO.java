package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {

    private String ingredientName;  // 성분명
    private Integer proteinAmount;  // 성분량

//    테이블에 추가해야하나?
    private String unit;  // 단위(mg, g)
    private String functionality; // 성분 기능 (예: "근육 성장", "면역력 향상")
    private String source; // 성분 출처 (예: "식물성", "동물성")
    private Double concentration; // 성분 농도 (예: 10.5%)

}

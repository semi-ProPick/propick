package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.ProductIngredientDetail;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    // 상세페이지용 dto

    private Integer productId;
    private String productName;  // 제품명
    private String brandName;  // 브랜드명
    private String productType;  // 제품유형
    private Integer productPrice;  // 제품 가격
    private List<String> productImages;  // 제품 이미지
    private Map<String, String> nutrients;  // 영양소 정보
    private List<IngredientWithInfoDTO> ingredientDTOs;  // 성분 정보
    private Integer calories;  // 칼로리
    private Integer servingSize;  // 1회 섭취량



}

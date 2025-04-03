package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.ProductInfo;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    // 상품 상세페이지용 dto

    private Integer productId;
    private String productName;  // 제품명
    private String brandName;  // 브랜드명
    private String productType;  // 제품유형
    private BigDecimal productPrice;  // 제품 가격
    private List<String> productImages;  // 제품 이미지
    private List<IngredientWithInfoDTO> ingredientDTOs;  // 상세페이지 성분 정보용
    private Integer calories;  // 칼로리
    private Integer servingSize;  // 1회 섭취량
    private Double proteinAmount; // 단백질 함량
    private Double proteinPer100g; // 100g 기준 단백질 함량
    private ProductInfo productInfo;  // ProductInfo를 추가

    // 할인율 (할인된 가격을 계산하기 위해 추가)
    private Integer discountRate;  // 할인율
    private BigDecimal discountedPrice;  // 할인된 가격

    //private List<ProductIngredientDTO> productIngredients;   // 상품- 성분 등록용
}

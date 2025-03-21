package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    // 상품 상세페이지 나올 dto

    private String productName;
    private String brandName;
    private String productType;
    private BigDecimal productPrice;
    private List<IngredientDTO> ingredients;
    private List<ProductImageDTO> productImages;


}

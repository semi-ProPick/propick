package com.ezen.propick.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductUpdateDTO {
    @NotNull(message = "상품 ID는 필수 항목입니다.")
    private Integer productId;    // 상품 ID (수정할 상품의 ID)

    @NotEmpty(message = "상품명은 필수 항목입니다.")
    private String productName;

    @NotNull(message = "브랜드 ID는 필수 항목입니다.")
    private Integer brandId;

    @NotEmpty(message = "제품 유형은 필수 항목입니다.")
    private String productType;

    @NotNull(message = "가격은 필수 항목입니다.")
    @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다.")
    private BigDecimal productPrice;

    @NotEmpty(message = "적어도 하나의 카테고리를 선택해야 합니다.")
    private List<Integer> categoryIds;  // 카테고리 리스트

    @NotEmpty(message = "최소 하나의 이미지 URL이 필요합니다.")
    private List<String> productImages; // 제품 이미지

    @NotEmpty(message = "성분 이름은 필수 항목입니다.")
    private List<String> ingredientNames;  // 성분 이름 리스트

    private Integer productInfoId; // 성분 상세정보 ID

    public ProductUpdateDTO(Integer productId, String productName, String productType,
                            BigDecimal productPrice, Integer brandId,
                            List<Integer> categoryIds, List<String> productImages,
                            List<String> ingredientNames, Integer productInfoId) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.productPrice = productPrice;
        this.brandId = brandId;
        this.categoryIds = categoryIds;
        this.productImages = productImages;
        this.ingredientNames = ingredientNames;
        this.productInfoId = productInfoId;
    }
}
package com.ezen.propick.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO {
    // 관리자 상품 수정용 dto

    private Integer productId;

    @NotEmpty(message = "상품명은 필수 항목입니다.")
    private String productName;  // 상품명

    @NotNull(message = "브랜드명은 필수 항목입니다.")
    private String brandName;  // 브랜드 이름 (선택용, 화면에만 표시)

    @NotEmpty(message = "제품 유형은 필수 항목입니다.")
    private String productType;  // 제품 유형

    @NotNull(message = "가격은 필수 항목입니다.")
    @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다.")
    private BigDecimal productPrice;  // 제품 가격

    @Size(min = 1, message = "적어도 하나의 카테고리를 선택해야 합니다.")
    private List<Integer> categoryIds;

    @NotEmpty(message = "최소 하나의 이미지가 필요합니다.")
    private List<String> productImages;  // 이미지 URL 리스트


    @NotEmpty(message = "성분 이름은 필수 항목입니다.")
    @Builder.Default
    private List<IngredientWithInfoDTO> ingredientDTOs = new ArrayList<>();  // 성분 이름과 성분량을 함께 처리하는 리스트

    private Integer calories;        // 칼로리
    private Integer discountRate;  // 할인율
    private Integer servingSize;   // 1회 섭취량
    private String imgUrl;



    private Integer ingredientDetailId;
    private Integer ingredientId;
    private String ingredientUnit;
    private String ingredientName;
    private BigDecimal ingredientAmount;
    private Integer productIngredientId;  // 각 상품의 연결된 성분들의 아이디


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientWithInfoDTO {
        private Integer ingredientDetailId;
        private Integer ingredientId;
        private String ingredientName;
        private String ingredientUnit;
        private BigDecimal ingredientAmount;
        private Integer productIngredientId;
    }

//    public Integer ingredientDetailId() {
//        return ingredientDetailId;
//    }
//    public void setIntegralDetailId(Integer ingredientDetailId) {
//        this.ingredientDetailId = ingredientDetailId;
//    }
//    public Integer ingredientId() {
//        return ingredientId;
//    }
//
//    public String ingredientName() {
//        return ingredientName;
//    }
//
//    public String ingredientUnit() {
//        return ingredientUnit;
//    }
//
//    public BigDecimal ingredientAmount() {
//        return ingredientAmount;
//    }

    }


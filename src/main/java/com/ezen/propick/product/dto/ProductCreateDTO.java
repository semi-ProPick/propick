package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.Brand;
import com.ezen.propick.product.entity.Ingredient;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ProductCreateDTO {
    // 상품 등록 dto

    @NotEmpty(message = "상품명은 필수 항목입니다.")
    private String productName;  // 상품명

    @NotNull(message = "브랜드 ID는 필수 항목입니다.")
    private Integer brandId;  // 브랜드 ID

    private String brandName;  // 브랜드 이름 (선택용, 화면에만 표시)

    @NotEmpty(message = "제품 유형은 필수 항목입니다.")
    private String productType;  // 제품 유형

    @NotNull(message = "가격은 필수 항목입니다.")
    @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다.")
    private BigDecimal productPrice;  // 제품 가격

    @NotEmpty(message = "적어도 하나의 카테고리를 선택해야 합니다.")
    private List<Integer> categoryIds;  // 선택된 카테고리 ID 리스트

    @NotEmpty(message = "최소 하나의 이미지가 필요합니다.")
    private List<String> productImages;  // 이미지 URL 리스트

    @NotEmpty(message = "성분 이름은 필수 항목입니다.")
    private List<String> ingredientNames;  // 성분 이름 리스트

    private BigDecimal discountPrice;      // 할인 가격

    private List<String> imageUrls;               // 기본 이미지 URL (필요시)

    @NotEmpty(message = "성분 이름은 필수 항목입니다.")
    private List<IngredientWithInfoDTO> ingredients;  // 성분 이름과 성분량을 함께 처리하는 리스트

    private Integer calories;        // 칼로리
    private Integer discountRate;  // 할인율
    private Integer servingSize;   // 1회 섭취량
    private Double proteinAmount;  // 단백질 함량
    private Integer ingredientId;  // 성분 아이디
    private String ingredientName; // 성분 이름
    private BigDecimal ingredientAmount;  // 성분량

    private Ingredient ingredient;


}

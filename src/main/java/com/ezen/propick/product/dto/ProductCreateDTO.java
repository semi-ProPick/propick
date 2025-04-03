package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.Ingredient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Lombok Setter 추가

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter // Lombok을 통해 Setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDTO {
    // 상품 등록용 dto

    @NotEmpty(message = "상품명은 필수 항목입니다.")
    private String productName;  // 상품명

//    @NotNull(message = "브랜드 ID는 필수 항목입니다.")
//    private Integer brandId;  // 브랜드 ID

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

//    private BigDecimal discountPrice;      // 할인 가격

    @NotEmpty(message = "성분 이름은 필수 항목입니다.")
    private List<IngredientWithInfoDTO> ingredientDTOs;  // 성분 이름과 성분량을 함께 처리하는 리스트

    private Integer calories;        // 칼로리
    private Integer discountRate;  // 할인율
    private Integer servingSize;   // 1회 섭취량
//    private Double proteinAmount;  // 단백질 함량

//    private List<Integer> ingredientIds;  // 성분 ID 리스트
//    private List<Integer> ingredientAmounts; // 성분량 리스트
}

package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.Ingredient;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Lombok Setter 추가

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDTO {
    // 관리자 상품 등록용

    @NotBlank(message = "상품명을 입력하세요.")
    private String productName;

    @NotBlank(message = "브랜드명을 입력하세요.")
    private String brandName;

    @NotBlank(message = "제품 유형을 입력하세요.")
    private String productType;

    @NotNull(message = "가격을 입력하세요.")
    @DecimalMin(value = "0.01", message = "가격은 0보다 커야 합니다.")
    private BigDecimal productPrice;

    @Size(min = 1, message = "카테고리를 하나 이상 선택하세요.")
    private List<Integer> categoryIds;

    @NotEmpty(message = "이미지는 최소 1개 이상 등록해야 합니다.")
    private List<String> productImages;

    @Size(min = 1, message = "성분을 하나 이상 추가하세요.")
    private List<IngredientWithInfoDTO> ingredientDTOs;

    @Min(value = 0, message = "칼로리는 0 이상이어야 합니다.")
    private Integer calories;

    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 100 이하로 입력하세요.")
    private Integer discountRate;

    @Min(value = 1, message = "1회 섭취량은 1 이상이어야 합니다.")
    private Integer servingSize;
}
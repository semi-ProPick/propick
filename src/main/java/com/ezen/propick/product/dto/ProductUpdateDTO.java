package com.ezen.propick.product.dto;

import jakarta.validation.constraints.*;
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
    // 관리자 상품 수정결과용 dto

    private Integer productId;

    @NotBlank(message = "상품명을 입력하세요.")
    private String productName;

    @NotBlank(message = "브랜드명을 입력하세요.")
    private String brandName;

    @NotBlank(message = "상품 유형을 입력하세요.")
    private String productType;

    @NotNull(message = "상품 가격을 입력하세요.")
    @DecimalMin(value = "0.01", message = "상품 가격은 0보다 커야 합니다.")
    private BigDecimal productPrice;

    @Size(min = 1, message = "카테고리를 하나 이상 선택해야 합니다.")
    private List<Integer> categoryIds;

    @NotEmpty(message = "이미지는 최소 1개 이상 등록해야 합니다.")
    private List<ProductImageDTO> productImages;

    @Size(min = 1, message = "성분을 하나 이상 추가하세요.")
    @Builder.Default
    private List<IngredientWithInfoDTO> ingredientDTOs = new ArrayList<>();

    @Min(value = 0, message = "칼로리는 0 이상이어야 합니다.")
    private Integer calories;

    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 100 이하로 입력하세요.")
    private Integer discountRate;

    @Min(value = 1, message = "1회 섭취량은 1 이상이어야 합니다.")
    private Integer servingSize;


    private Integer ingredientId;
    private String ingredientUnit;
    private String ingredientName;
    private BigDecimal ingredientAmount;
    private Integer productIngredientId;  // 각 상품의 연결된 성분들의 아이디


    //  내부 클래스에서 생성자 제거!
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientWithInfoDTO {
        private Integer ingredientId;
        private String ingredientName;
        private String ingredientUnit;
        private BigDecimal ingredientAmount;
        private Integer ProductIngredientId;

    }


    }


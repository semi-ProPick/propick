package com.ezen.propick.product.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String brandName;
    private String productType;
    private Integer productPrice;
    private List<String> productImages;
    private Map<String, String> nutrients;
    private List<IngredientWithInfoDTO> ingredientDTOs;
    private Integer calories;
    private Integer servingSize;
    private Integer discountRate;
    private Integer proteinAmount;
}
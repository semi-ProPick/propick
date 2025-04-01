package com.ezen.propick.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductListDTO {
    private Integer productId;
    private String productName;
    private String brandName;
    private String productType;
    private BigDecimal productPrice;
    private List<String> productImages;
    private boolean isBookmarked;

    public ProductListDTO(Integer productId, String productName, String brandName, String productType, BigDecimal productPrice, List<String> productImages) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.productType = productType;
        this.productPrice = productPrice;
        this.productImages = productImages;
        this.isBookmarked = false;
    }

    // 명시적으로 setIsBookmarked 메서드 추가
    public void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
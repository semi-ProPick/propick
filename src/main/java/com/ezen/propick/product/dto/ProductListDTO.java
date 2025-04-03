package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {
    // 상품 목록 페이지용 dto

    private Integer productId;
    private String productName;
    private String brandName;
    private String productType;
    private BigDecimal productPrice;  // 가격
    private List<String> productImages;
    private Integer discountRate;  // 할인율

    private BigDecimal discountedPrice; // 할인된 가격

    // 생성자
    public ProductListDTO(Integer productId, String productName, String brandName,
                          String productType, BigDecimal productPrice, Integer discountRate, List<String> productImages) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.productType = productType;
        this.productPrice = productPrice;
        this.discountRate = discountRate;
        this.productImages = productImages;
        this.discountedPrice = calculateDiscountedPrice(productPrice, discountRate);
    }

    // 할인된 가격 계산
    private BigDecimal calculateDiscountedPrice(BigDecimal productPrice, Integer discountRate) {
        if (productPrice == null || discountRate == null || discountRate == 0) {
            return productPrice;
        }
        BigDecimal discountAmount = productPrice.multiply(BigDecimal.valueOf(discountRate).divide(BigDecimal.valueOf(100)));
        return productPrice.subtract(discountAmount);
    }

}

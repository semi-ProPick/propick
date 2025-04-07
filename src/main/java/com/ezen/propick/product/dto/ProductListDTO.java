package com.ezen.propick.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ProductListDTO {
    // 상품 목록 페이지용 dto

    private Integer productId;
    private String productName;
    private String brandName;
    private String productType;
    private BigDecimal productPrice;
    private List<String> productImages;


    // 생성자
    public ProductListDTO(Integer productId, String productName, String brandName, String productType, BigDecimal productPrice, List<String> productImages) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.productType = productType;
        this.productPrice = productPrice;
        this.productImages = productImages;
    }
}

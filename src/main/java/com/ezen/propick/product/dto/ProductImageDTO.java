package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductImageDTO {
    private Integer productImageId;
    private String productImgUrl;
    private String productImgName;   // 이미지 이름

    // 필요한 생성자 추가
    public ProductImageDTO(Integer productImageId, String productImgUrl, String productImgName) {
        this.productImageId = productImageId;
        this.productImgUrl = productImgUrl;
        this.productImgName = productImgName;
    }

    // Getter 메서드
    public Integer getProductImageId() {
        return productImageId;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public String getProductImgName() {
        return productImgName;
    }
}

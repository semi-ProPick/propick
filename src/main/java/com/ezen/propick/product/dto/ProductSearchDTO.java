package com.ezen.propick.product.dto;

import com.ezen.propick.product.entity.ProductImage;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDTO {
    // 상품 검색결과 dto

    private String productName; // 상품명
    private BigDecimal productPrice;  // 상품가격
    private List<String> productImages;  // 상품 이미지
    private Integer productId;
    private String brandName;


    // getter, setter
    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }




}

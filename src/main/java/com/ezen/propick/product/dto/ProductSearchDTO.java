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
    // 상품 검색결과용 dto

    private Integer productId;  // 상품아이디
    private String productName; // 상품명
    private BigDecimal productPrice;  // 상품가격
    private List<String> productImages;  // 상품 이미지
    private String brandName; // 브랜드명

    private Integer discountRate;  // 할인율
    private BigDecimal discountedPrice; // 할인된 가격
    private boolean bookmarked;    // 북마크 여부


    // 할인된 가격 계산
    public void calculateDiscountedPrice() {
        if (this.productPrice != null && this.discountRate != null && this.discountRate > 0) {
            BigDecimal discountAmount = this.productPrice.multiply(BigDecimal.valueOf(this.discountRate).divide(BigDecimal.valueOf(100)));
            this.discountedPrice = this.productPrice.subtract(discountAmount);
        } else {
            this.discountedPrice = this.productPrice;  // 할인율이 없으면 원래 가격 그대로
        }
    }
}

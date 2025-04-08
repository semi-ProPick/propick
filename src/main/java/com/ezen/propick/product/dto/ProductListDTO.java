package com.ezen.propick.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductListDTO {
    // 상품 목록 결과용 DTO

    private Integer productId;     // 상품 아이디
    private String productName;    // 상품명
    private String brandName;      // 브랜드명
    private String productType;    // 상품 유형
    private BigDecimal productPrice;  // 가격
    private List<String> productImages;  // 상품 이미지
    private Integer discountRate;  // 할인율
    private boolean bookmarked;    // 북마크 여부
    private BigDecimal discountedPrice; // 할인된 가격
    private LocalDateTime productCreatedAt; // 등록 시간
    private List<String> categories; // 카테고리명 리스트
    private Integer bookmarkCount;  // 북마크 수 (추가된 필드)

    // 생성자
    public ProductListDTO(Integer productId, String productName, String brandName,
                          String productType, BigDecimal productPrice, Integer discountRate,
                          List<String> productImages, LocalDateTime productCreatedAt, List<String> categories) {
        this.productId = productId;
        this.productName = productName;
        this.brandName = brandName;
        this.productType = productType;
        this.productPrice = productPrice;
        this.discountRate = discountRate;
        this.productImages = productImages;
        this.discountedPrice = calculateDiscountedPrice(productPrice, discountRate);
        this.productCreatedAt = productCreatedAt;
        this.categories = categories;
        this.bookmarkCount = 0; // 기본값으로 0 설정
    }

    // 상품의 원래 가격과 할인율을 받아서 최종 할인된 가격으로 반환하는 메서드
    private BigDecimal calculateDiscountedPrice(BigDecimal productPrice, Integer discountRate) {
        if (productPrice == null || discountRate == null || discountRate == 0) {
            return productPrice;
        }
        BigDecimal discountAmount = productPrice.multiply(BigDecimal.valueOf(discountRate).divide(BigDecimal.valueOf(100)));
        return productPrice.subtract(discountAmount);
    }
}
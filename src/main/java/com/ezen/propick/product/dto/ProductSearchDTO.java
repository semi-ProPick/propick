package com.ezen.propick.product.dto;

import java.math.BigDecimal;

public class ProductSearchDTO {
    // 상품 검색용 dto
    private String brandName;  // 브랜드명
    private String productName; // 상품명
    private String categoryName; // 카테고리
    private String IngredientName;  // 성분명
    private BigDecimal maxPrice;  // 최고가격
    private BigDecimal minPrice;  // 최저가격

}

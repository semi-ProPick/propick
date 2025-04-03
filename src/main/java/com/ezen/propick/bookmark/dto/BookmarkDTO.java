package com.ezen.propick.bookmark.dto;

import com.ezen.propick.product.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private Integer bookmarkId; // bookmark_id와 매핑
    private String bookmarkStatus; // bookmark_status와 매핑 (Enum을 String으로 처리)
    private String userNo; // user_no와 매핑
    private Integer productId; // product_id와 매핑
//    private String productName; // 제품 이름 (마이페이지 표시용)
//    private BigDecimal productPrice; // 제품 가격 (마이페이지 표시용)
//    private String brandName;

    //private Product product;   // 상품 정보


    @Override
    public String toString() {
        return "BookmarkDTO{" +
                "id=" + bookmarkId +
                ", status='" + bookmarkStatus + '\'' +
                ", userNo=" + userNo +
                '}';
    }
}
package com.ezen.propick.bookmark.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private Integer id; // bookmark_id와 매핑
    private String status; // bookmark_status와 매핑 (Enum을 String으로 처리)
    private Integer userNo; // user_no와 매핑
    private Integer productId; // product_id와 매핑

    @Override
    public String toString() {
        return "BookmarkDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", userNo=" + userNo +
                ", productId=" + productId +
                '}';
    }
}
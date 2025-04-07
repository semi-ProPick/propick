package com.ezen.propick.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Integer productImageId;   // 이미지 아이디
    private String productImgUrl;     // 이미지 url 경로
    private String productImgName;   // 이미지 이름

}

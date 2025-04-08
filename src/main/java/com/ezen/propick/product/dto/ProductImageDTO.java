package com.ezen.propick.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Integer productImageId;
    private String productImgUrl;
    private String productImgName;   // 이미지 이름


}

package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_id")
    private Integer productImgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "img_url", nullable = false, length = 500)
    private String imgUrl;

    @Column(name = "product_img_name", nullable = false, length = 255)
    private String productImgName;

    // imgUrl에 대한 getter 메서드 추가
    public String getImageUrl() {
        return imgUrl;
    }

    // product와 imgUrl을 받는 생성자 추가
//    public ProductImage(Product product, String imgUrl, String productImgName) {
//        this.product = product;
//        this.imgUrl = imgUrl;
//        this.productImgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);  // 이미지 파일 이름을 추출
//    }
}

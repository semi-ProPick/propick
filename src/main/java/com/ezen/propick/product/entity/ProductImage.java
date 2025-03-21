package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_id")
    private Integer productImgId;   // 제품 이미지 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)   // 외래 키
    private Product product;

    @Column(name = "product_img_name", nullable = false)
    private String productImgName;

    @Column(name = "img_url",nullable = false)
    private String productImgUrl;

}

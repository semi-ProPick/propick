package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_info_id")
    private Integer productInfoId;

//    @OneToOne(mappedBy = "productInfo", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Product product; // Product와 양방향 관계 설정

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate = 0;      // 할인

    @Column(name = "serving_size", nullable = false)
    private Integer servingSize;       // 1회 섭취량

    @Column(name = "calories", nullable = false)
    private Integer calories;         // 칼로리(에너지)

    @Column(name = "nutrients", nullable = false, length = 100, columnDefinition = "TEXT")
    private String nutrients;  // 영양소

    @Column(name = "protein_amount")
    private Double proteinAmount;    // 프로틴 함량


    // 양방향 관계를 위해 setProduct 추가
    public ProductInfo setProduct(Product product) {
        this.product = product;
        if (product != null && product.getProductInfo() != this) { // 무한 루프 방지
            product.setProductInfo(this);
        }
        return this; // 추가
    }
}






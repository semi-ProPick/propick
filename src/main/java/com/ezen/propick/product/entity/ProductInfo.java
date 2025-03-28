package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products_info")
@Getter
@Setter
@NoArgsConstructor
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_info_id")
    private Integer productInfoId;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;

    @OneToOne(mappedBy = "productInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Product product; // Product와 양방향 관계 설정

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;      // 할인

    @Column(name = "serving_size", nullable = false)
    private Integer servingSize;       // 1회 섭취량

    @Column(name = "calories", nullable = false)
    private Integer calories;         // 칼로리(에너지)

    @Column(name = "nutrients", nullable = false, length = 100,columnDefinition = "TEXT")
    private String nutrients;  // Nutrients stored as JSON.  영양소

    @Column(name = "protein_amount")
    private Double proteinAmount;    // 프로틴 함량

    public Double getProteinAmount() {
        return proteinAmount;
    }

    public void setProteinAmount(Double proteinAmount) {
        this.proteinAmount = proteinAmount;
    }
}



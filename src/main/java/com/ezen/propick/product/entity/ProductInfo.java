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
    private Integer discountRate;

    @Column(name = "serving_size", nullable = false)
    private Integer servingSize;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "nutrients", nullable = false, length = 100,columnDefinition = "TEXT")
    private String nutrients;  // Nutrients stored as JSON

    @Column(name = "protein_amount")
    private Integer proteinAmount;

    public Integer getProteinAmount() {
        return proteinAmount;
    }

    public void setProteinAmount(Integer proteinAmount) {
        this.proteinAmount = proteinAmount;
    }
}



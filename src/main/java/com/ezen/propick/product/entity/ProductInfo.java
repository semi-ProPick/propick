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
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Product와의 관계 설정 (소유자)

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Column(name = "serving_size", nullable = false)
    private Integer servingSize;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "nutrients", nullable = false, columnDefinition = "TEXT")
    private String nutrients;  // Nutrients stored as JSON

    @Column(name = "protein_amount")
    private Integer proteinAmount;
}
package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;  // 성분 아아디

    @Column(name = "ingredient", nullable = false, length = 255)
    private String ingredientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)  // 외래 키
    private Product product;    // 제품 하나당 여러 성분

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protein_info_id", nullable = false)  // 외래 키
    private ProductInfo productInfo;

}

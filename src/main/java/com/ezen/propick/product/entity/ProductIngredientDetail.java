package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_ingredient_detail")
@Getter
@Setter
@NoArgsConstructor
public class ProductIngredientDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ingredient_id")
    private Integer productIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "ingredient_amount", nullable = false)
    private BigDecimal ingredientAmount;

    @Column(name = "ingredient_unit", nullable = false, length = 10)
    private String ingredientUnit;
}


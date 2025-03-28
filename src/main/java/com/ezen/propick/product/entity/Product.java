package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id", nullable = false)
    private Brand brand;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_created_at")
    private LocalDateTime productCreatedAt;

    @Column(name = "product_updated_at")
    private LocalDateTime productUpdatedAt;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @Column(name = "product_price")
    private Integer productPrice;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductInfo productInfo;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductIngredientDetail> productIngredientDetails = new ArrayList<>();
}
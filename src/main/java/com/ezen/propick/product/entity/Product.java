package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id", nullable = false)
    private Brand brand;  // 브랜드 참조

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> productCategories = new ArrayList<>();  // 카테고리 참조

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();   // 이미지 참조

    @Column(name = "product_created_at" ,nullable = false, updatable = false)
    private LocalDateTime productCreatedAt;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductInfo productInfo;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductIngredientDetail> productIngredientDetails = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.productCreatedAt = LocalDateTime.now();  // 등록 시간 자동 설정
    }

    // 검색 시
    @Override
    public boolean equals(Object o) { // 두 객체가 같은지 비교할 때 기준을 직접 정하겠다
        if (this == o) return true;    // 자기 자신과 비교하는 경우 같음
        if (o == null || getClass() != o.getClass()) return false;   // 비교 대상이 null이거나, 클래스가 Product와 다르면 같지 않음
        Product product = (Product) o;      //비교할 객체를 Product 타입으로 캐스팅하고 productId 값이 같으면 같은 상품이라고 판단
        return Objects.equals(productId, product.productId);  //상품 ID만 같으면 같은 상품으로 본다
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    // equals()를 오버라이딩하면, 반드시 hashCode()도 같이 오버라이딩해야 한다.
    // productId 값을 기준으로 고유한 해시값을 생성


}
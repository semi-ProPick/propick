package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)  // 자동 시간 설정을 위해 추가
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;  // 상품 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id", nullable = false)  // 외래 키
    private Brand brand;  // 상품과 브랜드 연결

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY) // 외래 키
    private List<ProductImage> productImages;    // 상품과 상품이미지 연결

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductCategory> productCategories;  // 상품과 카테고리를 연결

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name ="product_price", nullable =false, precision = 10, scale = 2)  // DECIMAL(10,2) 설정
    private BigDecimal productPrice;

    @CreatedDate  //생성시간 자동 저장
    @Column(name = "product_created_at", nullable = false, updatable = false)
    private LocalDateTime productCreatedAt;

    @LastModifiedDate  // 수정 시간 자동 저장
    @Column(name = "product_updated_at")
    private LocalDateTime productUpdatedAt;

    @Column(name = "product_type", nullable = false, length = 100)  // 제품 유형
    private String productType;
}

package com.ezen.propick.product.entity;

import com.ezen.propick.product.entity.Brand;
import com.ezen.propick.product.entity.ProductImage;
import com.ezen.propick.product.entity.ProductInfo;
import com.ezen.propick.product.entity.ProductIngredientDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id", nullable = false)
    private Brand brand;  // 브랜드 참조

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

//    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private ProductInfo productInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // 외래키를 ProductInfo 테이블에 설정
    private ProductInfo productInfo;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductIngredientDetail> productIngredientDetails = new ArrayList<>();

    // getter
    public Integer getProductPrice() {
        return productPrice.intValue();  // BigDecimal을 Integer로 변환
    }

    // setter
    public void setProductPrice(Integer productPrice) {
        this.productPrice = BigDecimal.valueOf(productPrice);  // Integer를 BigDecimal로 변환
    }
}

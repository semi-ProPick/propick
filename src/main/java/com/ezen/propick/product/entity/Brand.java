package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "brand")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @CreatedDate
    @Column(name = "brand_created_at" ,nullable = false, updatable = false)
    private LocalDateTime brandCreatedAt;

    @Column(name = "brand_status")
    private String brandStatus;

    @Column(name = "brand_phone")
    private String brandPhone;

    @Column(name = "brand_url")
    private String brandUrl;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)  // 외래 키
    private List<Product> products;   // 브랜드 와 상품 연결

    @PrePersist
    public void prePersist() {
        if (this.brandCreatedAt == null) {
            this.brandCreatedAt = LocalDateTime.now();  // 현재 시간으로 설정
        }
    }
}

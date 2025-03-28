package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(name = "brand_created_at", nullable = false, updatable = false)
    private LocalDateTime brandCreatedAt;

    @Column(name = "brand_status", nullable = false)
    private String brandStatus;

    @Column(name = "brand_phone", nullable = false)
    private String brandPhone;

    @Column(name = "brand_url", nullable = false)
    private String brandUrl;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
}

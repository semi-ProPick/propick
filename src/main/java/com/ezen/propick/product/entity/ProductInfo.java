package com.ezen.propick.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products_Info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "protein_info_id")
    private Integer proteinInfoId;    // 제품 성분 아이디

    @Column(name = "protein_amount", nullable = false)
    private Integer proteinAmount;    // 제품 성분 양

    @OneToMany(mappedBy = "productInfo", fetch = FetchType.LAZY)   // 외래 키
    private List<Ingredient> ingredients;    // 각 성분에 제품 성분 하나
}

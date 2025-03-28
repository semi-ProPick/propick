package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Brand;
import com.ezen.propick.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // 특정 브랜드명으로 상품 조회
    List<Product> findByBrandBrandNameContaining(String brandName);

    // 특정 상품명으로 상품 검색
    List<Product> findByProductNameContaining(String keyword);
}
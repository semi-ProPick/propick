package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // 상품 데이터 접근해서 조회, CRUD

    // 상품명으로 조회
    List<Product> findByProductNameContainin(String productName);

    // 브랜드명으로 조회
    List<Product> findByBrandName(String brandName);

    // 제품유형으로 조회
    List<Product> findByProductType(String productType);

}

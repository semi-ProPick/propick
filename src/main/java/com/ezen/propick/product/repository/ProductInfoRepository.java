package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Integer> {
    // 특정 상품의 상세 정보 조회
    Optional<ProductInfo> findByProduct(Product product);
}

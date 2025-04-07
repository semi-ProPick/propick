package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    // 특정 상품의 이미지 리스트 조회
    List<ProductImage> findByProduct(Product product);
}
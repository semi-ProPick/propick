package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    // 상품과 카테고리 간의 관계 관리

    // 상품 id 기준으로 해당되는 카테고리 id 리스트 조회
    @Query("SELECT pc.category.categoryId FROM ProductCategory pc WHERE pc.product.productId = :productId")
    List<Integer> findCategoryIdsByProductId(@Param("productId") Integer productId);

    // 카테고리 삭제 (관리자)
    void deleteByProduct(@Param("product") Product product);
}
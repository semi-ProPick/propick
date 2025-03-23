package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    // 상품과 카테고리 간의 관계를 관리하고, 조회만 하는 역할

    // 카테고리 이름으로 조회
    List<ProductCategory> findByCategoryName(String categoryName);
}

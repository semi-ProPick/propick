package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    // 상품과 카테고리 간의 관계 관리

}

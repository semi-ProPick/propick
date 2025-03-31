package com.ezen.propick.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

    // 상품명으로 검색
    @Query("SELECT p FROM Product p JOIN FETCH p.productImages pi WHERE p.productName LIKE %:keyword%")
    List<Product> findByProductNameContaining(@Param("keyword") String keyword);

    // 브랜드명으로 검색
    @Query("SELECT p FROM Product p JOIN FETCH p.productImages pi WHERE p.brand.brandName LIKE %:keyword%")
    List<Product> findByBrandNameContaining(@Param("keyword") String keyword);

    // 프로틴유형으로 검색
    @Query("SELECT p FROM Product p WHERE p.productType LIKE %:keyword%")
    List<Product> findByProductTypeContaining(@Param("keyword") String keyword);

    // 성분명으로 검색
    @Query("SELECT p FROM Product p JOIN p.productIngredientDetails pid JOIN pid.ingredient i WHERE i.ingredientName LIKE %:keyword%")
    List<Product> findByIngredientNameContaining(@Param("keyword") String keyword);
}

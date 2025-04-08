package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

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

    // 전체 상품 중 할인 상품 조회
    @Query("SELECT p FROM Product p WHERE p.productInfo.discountRate > :discountRate")
    List<Product> findByDiscountRateGreaterThan(double discountRate);

    // 카테고리별로 상품 조회
    @Query("SELECT DISTINCT p FROM Product p JOIN p.productCategories pc WHERE pc.category.categoryId = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);

    // 할인 + 카테고리 상품 조회
    @Query("SELECT DISTINCT p FROM Product p JOIN p.productCategories pc " +
            "WHERE p.productInfo.discountRate > 0 AND pc.category.categoryId = :categoryId")
    List<Product> findDiscountedProductsByCategoryId(@Param("categoryId") Integer categoryId);

    // 관리자 검색 기능
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword% OR p.brand.brandName LIKE %:keyword% ORDER BY p.productCreatedAt DESC")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 관리자 상품 삭제
    void deleteById(Integer productId);




}
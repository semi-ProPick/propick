//package com.ezen.propick.product.service;
//
//import com.ezen.propick.product.dto.ProductListDTO;
//import com.ezen.propick.product.dto.ProductSearchDTO;
//import com.ezen.propick.product.entity.Brand;
//import com.ezen.propick.product.entity.Product;
//import com.ezen.propick.product.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ProductSearchService {
//    private final ProductRepository productRepository;
//
//    // 특정 브랜드의 모든 상품 조회
//    public List<ProductListDTO> searchByBrandName(String brandName) {
//
//        List<Product> products = productRepository.findByBrandNameContaining(brandName);
//
//    }
//    // 특정 이름을 포함하는 상품 검색  findByProductNameContaining
//
//    // 특정 성분을 포함하는 모든 상품 조회 findByIngredient
//
//    // 특정 카테고리에 속한 상품 조회 → findByCategory(Category category)
//
//    // 특정 성분이 포함된 상품 검색 → findByIngredient(Ingredient ingredient)
//
//    // 특정 성분이 없는 상품 검색 → findByIngredientNotContaining(Ingredient ingredient)
//
//    // 특정 가격 이상 → findByPriceGreaterThanEqual(Integer price)
//
//    // 특정 가격 이하 → findByPriceLessThanEqual(Integer price)
//
//    // 특정 가격 범위 → findByPriceBetween(Integer min, Integer max)
//
//    // 할인이 적용된 상품 조회 → findByDiscountRateGreaterThan(Integer discountRate)
//
//    // 가격순 정렬 (낮은 가격부터) → OrderByPriceAsc
//
//    //가격순 정렬 (높은 가격부터) → OrderByPriceDesc
//
//    //인기순 정렬 (예: 많이 조회된 순서) → OrderByViewCountDesc
//}

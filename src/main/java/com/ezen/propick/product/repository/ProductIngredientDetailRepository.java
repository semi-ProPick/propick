package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Ingredient;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductIngredientDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductIngredientDetailRepository extends JpaRepository<ProductIngredientDetail, Integer> {

    static void deleteByProduct(Product product) {
        ProductIngredientDetailRepository.deleteByProduct(product);
    }

    // 특정 상품의 모든 성분 정보 조회
    List<ProductIngredientDetail> findByProduct(Product product);

}

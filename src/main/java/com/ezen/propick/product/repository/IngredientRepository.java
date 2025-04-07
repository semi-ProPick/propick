package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Ingredient;
import com.ezen.propick.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    // 특정 성분명으로 검색
    //Optional<Ingredient> findByIngredientName(String ingredientName);
}


package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Category;
import com.ezen.propick.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}

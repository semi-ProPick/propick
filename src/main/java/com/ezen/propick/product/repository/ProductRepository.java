package com.ezen.propick.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {  // Long → Integer 변경
}

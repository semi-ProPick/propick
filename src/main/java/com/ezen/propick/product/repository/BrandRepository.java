package com.ezen.propick.product.repository;

import com.ezen.propick.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    // 브랜드명 조회
    Optional<Brand> findByBrandName(String brandName);
}

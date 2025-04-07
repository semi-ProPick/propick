package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.ProductCreateDTO;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.entity.Brand;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.BrandRepository;
import com.ezen.propick.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    // 상품  등록, 조회, 수정, 삭제

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 등록
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {

        // brandId로 브랜드 조회 (없으면 예외 발생)
        Brand brand = brandRepository.findById(productCreateDTO.getBrandId())
                .orElseThrow(()-> new IllegalArgumentException("해당 브랜드 찾을 수 없음"));

        // dto -> 엔티티로 변환
        Product product = Product.builder()
                .productName(productCreateDTO.getProductName())
                .brand(brand)  // 조회한 브랜드 설정
                .productPrice(productCreateDTO.getProductPrice())
                .productType(productCreateDTO.getProductType())
                .build();

        // DB에 저장
        product = productRepository.save(product);

        // Lazy Loading으로 인해 brand를 초기화
        // @ManyToOne(fetch = FetchType.LAZY) 설정이므로 브랜드 정보가 즉시 로드되지 않음
        String brandName = product.getBrand().getBrandName();  // 여기서 brand 초기화됨

        // 엔티티 -> dto 변환
        return ProductDTO.builder()
                .productName(product.getProductName())
                .brandName(brandName)
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .build();
    }
}


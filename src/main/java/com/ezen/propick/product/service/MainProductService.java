package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.IngredientWithInfoDTO;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.ProductImageRepository;
import com.ezen.propick.product.repository.ProductInfoRepository;
import com.ezen.propick.product.repository.ProductIngredientDetailRepository;
import com.ezen.propick.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductInfoRepository productInfoRepository;
    private final ProductIngredientDetailRepository productIngredientDetailRepository;

    /* 전체 상품 목록 조회 */
    @Transactional
    public List<ProductListDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();  // DB에서 모든 상품 조회

        // 상품 목록을 DTO로 변환
        return products.stream().map(product ->
                new ProductListDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getBrand().getBrandName(),
                        product.getProductType(),
                        product.getProductPrice(),
                        productImageRepository.findByProduct(product).stream()  // 상품에 속한 이미지 목록 조회
                                .map(ProductImage::getImageUrl)  // 이미지 URL만 추출
                                .collect(Collectors.toList())  // 리스트로 반환
                )
        ).collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO getProductDetailById(Integer productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        // 2. 상품 상세 정보 조회
        Optional<ProductInfo> optionalProductInfo = productInfoRepository.findByProduct(product);
        ProductInfo productInfo = optionalProductInfo.orElse(null); // null 체크 후 사용

        if (optionalProductInfo.isEmpty()) {
            System.out.println("⚠ No ProductInfo found for productId: " + productId);
        } else {
//            ProductInfo productInfo = optionalProductInfo.get();
            System.out.println("✅ ProductInfo found: " + productInfo);
            System.out.println("Calories: " + productInfo.getCalories());
            System.out.println("Serving Size: " + productInfo.getServingSize());
        }
        // 3. 상품 성분 정보 조회
        List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(product);

        // 4. DTO 변환 (영양소 정보 Map 생성)
        Map<String, String> nutrientsMap = new HashMap<>();
        if (productInfo != null && productInfo.getNutrients() != null) {
            // ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                nutrientsMap = objectMapper.readValue(productInfo.getNutrients(), new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                e.printStackTrace(); // JSON 파싱 오류 처리
            }
        }

        // 5. 성분 정보 DTO 변환
        List<IngredientWithInfoDTO> ingredientDetailDTOs = ingredientDetails.stream()
                .map(detail -> new IngredientWithInfoDTO(
                        detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : "Unknown",
                        detail.getIngredientAmount() != null ? detail.getIngredientAmount() : BigDecimal.ZERO,
                        detail.getIngredientUnit() != null ? detail.getIngredientUnit() : "N/A"
                ))
                .collect(Collectors.toList());

        // 6. ProductDTO 생성 및 반환
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown")
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .productImages(product.getProductImages().stream()
                        .map(image -> image.getImageUrl())
                        .collect(Collectors.toList()))
                .nutrients(nutrientsMap)
                .ingredientDTOs(ingredientDetailDTOs)
                .calories(productInfo != null ? productInfo.getCalories() : null)
                .servingSize(productInfo != null ? productInfo.getServingSize() : null)
                .build();

    }


}

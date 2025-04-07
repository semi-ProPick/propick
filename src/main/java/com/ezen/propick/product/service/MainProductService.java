package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.IngredientWithInfoDTO;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
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
    // 선택한 상품 상세 페이지 조회
    @Transactional
    public ProductDTO getProductDetailById(Integer productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        // 2. 상품 상세 정보 조회
        Optional<ProductInfo> optionalProductInfo = productInfoRepository.findByProduct(product);
        ProductInfo productInfo = optionalProductInfo.orElse(new ProductInfo()); // null 체크 후 사용


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

        // 6. 100g 기준 단백질 함량 계산
        Double proteinPer100g = 0.0;
        if (productInfo != null) {
            proteinPer100g = calculateProteinPer100g(productInfo);
        }

        // 7. ProductDTO 생성 및 반환
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
                .calories(productInfo.getCalories())  // 이미 null 체크가 되어 있으므로 직접 호출
                .servingSize(productInfo.getServingSize())  // 동일하게 null 처리 없이 사용
                .proteinAmount(productInfo.getProteinAmount())  // 단백질 함량
                .proteinPer100g(proteinPer100g)  // 계산된 100g 기준 단백질 함량 추가
                .productInfo(productInfo)  // productInfo 포함
                .build();

    }
    // 영양소 정보를 JSON 문자열에서 Map으로 변환하는 메서드
    private Map<String, String> convertNutrientsToMap(String nutrientsJson) {
        Map<String, String> nutrientsMap = new HashMap<>();
        if (nutrientsJson != null && !nutrientsJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                nutrientsMap = objectMapper.readValue(nutrientsJson, new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                e.printStackTrace();  // JSON 파싱 오류 처리
            }
        }
        return nutrientsMap;
    }

    // 단백질 100g당 함량 계산 = (단백질함량/1회섭취량) * 100
    public Double calculateProteinPer100g(ProductInfo productInfo) {
        if (productInfo.getServingSize() != null && productInfo.getProteinAmount() != null && productInfo.getServingSize() > 0) {
            return (double) productInfo.getProteinAmount() / productInfo.getServingSize() * 100;
        }
        return 0.0; // 기본값 (0.0 또는 null 반환 등)
    }

    // 검색
    @Transactional
    public List<ProductSearchDTO> getProductBySearchKeyword(String keyword) {
        // 상품명
        List<Product> productsByName = productRepository.findByProductNameContaining(keyword);
        // 브랜드명
        List<Product> productsByBrand = productRepository.findByBrandNameContaining(keyword);
        // 프로틴 유형
        List<Product> productsByType = productRepository.findByProductTypeContaining(keyword);

        // 성분명
        List<Product> productByIngredient = productRepository.findByIngredientNameContaining(keyword);

        Set<Product> mergedProducts = new HashSet<>();

        mergedProducts.addAll(productsByName);
        mergedProducts.addAll(productsByBrand);
        mergedProducts.addAll(productsByType);
        mergedProducts.addAll(productByIngredient);

        return mergedProducts.stream().map(product -> {
            List<String> imageUrls = product.getProductImages()
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());

            return new ProductSearchDTO(
                    product.getProductName(),
                    product.getProductPrice(),
                    imageUrls,
                    product.getProductId(),
                    product.getBrand().getBrandName()
            );
        }).collect(Collectors.toList());

    }

}


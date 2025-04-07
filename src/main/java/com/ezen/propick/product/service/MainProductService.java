package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.*;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductInfo;
import com.ezen.propick.product.entity.ProductIngredientDetail;
import com.ezen.propick.product.entity.ProductImage;
import com.ezen.propick.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainProductService {

    private final ProductRepository productRepository;

    // 모든 상품 조회
    public List<ProductListDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToProductListDTO)
                .collect(Collectors.toList());
    }

    // 할인된 상품 조회
    public List<ProductListDTO> getDiscountedProducts() {
        List<Product> products = productRepository.findByDiscountRateGreaterThan(0);
        return products.stream()
                .map(this::convertToProductListDTO)
                .collect(Collectors.toList());
    }

    // 상품 상세 조회
    public ProductDTO getProductDetailById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: productId=" + productId));
        return convertToProductDTO(product);
    }

    // 검색
    public List<ProductSearchDTO> getProductBySearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        // 상품명, 브랜드명, 프로틴 유형, 성분명으로 검색
        Set<Product> matchedProducts = new HashSet<>();

        // 상품명으로 검색
        List<Product> byProductName = productRepository.findByProductNameContaining(keyword);
        matchedProducts.addAll(byProductName);

        // 브랜드명으로 검색
        List<Product> byBrandName = productRepository.findByBrandNameContaining(keyword);
        matchedProducts.addAll(byBrandName);

        // 프로틴 유형으로 검색
        List<Product> byProductType = productRepository.findByProductTypeContaining(keyword);
        matchedProducts.addAll(byProductType);

        // 성분명으로 검색
        List<Product> byIngredientName = productRepository.findByIngredientNameContaining(keyword);
        matchedProducts.addAll(byIngredientName);

        return matchedProducts.stream()
                .map(this::convertToProductSearchDTO)
                .collect(Collectors.toList());
    }

    // 성분별 100g당 함량 계산
    public Map<String, BigDecimal> calculateIngredientsPer100g(ProductInfo productInfo, List<ProductIngredientDetail> ingredientDetails) {
        if (productInfo == null || ingredientDetails == null || ingredientDetails.isEmpty()) {
            return Map.of();
        }

        Map<String, BigDecimal> ingredientsPer100g = new HashMap<>();

        // servingSize를 기준으로 100g당 함량 계산
        Integer servingSize = productInfo.getServingSize();
        if (servingSize == null || servingSize <= 0) {
            servingSize = 100; // 기본값
        }

        // ProductIngredientDetail에서 성분별 함량 계산
        for (ProductIngredientDetail detail : ingredientDetails) {
            String ingredientName = detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : null;
            BigDecimal ingredientAmount = detail.getIngredientAmount(); // 총 함량
            String unit = detail.getIngredientUnit(); // 단위 (g 또는 mg)

            if (ingredientName == null || ingredientAmount == null || unit == null) {
                continue; // 데이터가 누락된 경우 건너뜀
            }

            // 단위 변환: mg → g
            BigDecimal amountInGrams;
            if ("mg".equalsIgnoreCase(unit)) {
                amountInGrams = ingredientAmount.divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_HALF_UP); // mg을 g으로 변환
            } else if ("g".equalsIgnoreCase(unit)) {
                amountInGrams = ingredientAmount; // g 단위 그대로 사용
            } else {
                continue; // 지원하지 않는 단위는 건너뜀
            }

            // 100g당 함량 계산: (amount / servingSize) * 100
            BigDecimal amountPer100g = amountInGrams
                    .divide(BigDecimal.valueOf(servingSize), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            ingredientsPer100g.put(ingredientName, amountPer100g);
        }

        // proteinAmount는 별도로 계산
        if (productInfo.getProteinAmount() != null) {
            BigDecimal proteinPer100g = BigDecimal.valueOf(productInfo.getProteinAmount())
                    .divide(BigDecimal.valueOf(servingSize), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            ingredientsPer100g.put("Protein", proteinPer100g);
        }

        return ingredientsPer100g;
    }

    // Product -> ProductListDTO 변환
    public ProductListDTO convertToProductListDTO(Product product) {
        // ProductInfo에서 discountRate 가져오기
        Integer discountRate = null;
        ProductInfo productInfo = product.getProductInfo();
        if (productInfo != null) {
            discountRate = productInfo.getDiscountRate();
        }

        return new ProductListDTO(
                product.getProductId(),
                product.getProductName(),
                product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown Brand",
                product.getProductType(),
                product.getProductPrice(),
                discountRate,
                product.getProductImages().stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList())
        );
    }

    // Product -> ProductDTO 변환
    private ProductDTO convertToProductDTO(Product product) {
        ProductInfo productInfo = product.getProductInfo();
        Integer discountRate = productInfo != null ? productInfo.getDiscountRate() : null;
        BigDecimal discountedPrice = null;

        // 할인된 가격 계산
        if (discountRate != null && discountRate > 0 && product.getProductPrice() != null) {
            BigDecimal price = product.getProductPrice();
            BigDecimal discount = price.multiply(BigDecimal.valueOf(discountRate)).divide(BigDecimal.valueOf(100));
            discountedPrice = price.subtract(discount);
        }

        // 성분 정보 매핑
        List<ProductIngredientDetail> ingredientDetails = product.getProductIngredientDetails();
        List<IngredientWithInfoDTO> ingredientDTOs = ingredientDetails.stream()
                .map(detail -> {
                    String ingredientName = detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : null;
                    BigDecimal ingredientAmount = detail.getIngredientAmount();
                    String unit = detail.getIngredientUnit();

                    return IngredientWithInfoDTO.builder()
                            .ingredientName(ingredientName)
                            .ingredientAmount(ingredientAmount)
                            .ingredientUnit(unit)
                            .build();
                })
                .collect(Collectors.toList());

        // ProductIngredientDTO 매핑
        List<ProductIngredientDTO> productIngredients = ingredientDetails.stream()
                .map(detail -> ProductIngredientDTO.builder()
                        .ingredientId(detail.getIngredient() != null ? detail.getIngredient().getIngredientId() : null)
                        .ingredientAmount(detail.getIngredientAmount() != null ? detail.getIngredientAmount().intValue() : null)
                        .ingredientUnit(detail.getIngredientUnit())
                        .build())
                .collect(Collectors.toList());

        // 100g당 단백질 함량 계산
        Double proteinPer100g = null;
        if (productInfo != null && productInfo.getProteinAmount() != null && productInfo.getServingSize() != null && productInfo.getServingSize() > 0) {
            proteinPer100g = (productInfo.getProteinAmount() / productInfo.getServingSize()) * 100;
        }

        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown Brand")
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .productImages(product.getProductImages().stream()
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList()))
                .ingredientDTOs(ingredientDTOs)
                .calories(productInfo != null ? productInfo.getCalories() : null)
                .servingSize(productInfo != null ? productInfo.getServingSize() : null)
                .proteinAmount(productInfo != null ? productInfo.getProteinAmount() : null)
                .proteinPer100g(proteinPer100g)
                .productInfo(productInfo)
                .discountRate(discountRate)
                .discountedPrice(discountedPrice)
                .productIngredients(productIngredients)
                .build();
    }

    // Product -> ProductSearchDTO 변환
    private ProductSearchDTO convertToProductSearchDTO(Product product) {
        ProductSearchDTO dto = new ProductSearchDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        return dto;
    }
}
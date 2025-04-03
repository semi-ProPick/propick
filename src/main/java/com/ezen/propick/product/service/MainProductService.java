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

    // 전체 상품 목록 조회
    public List<ProductListDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();  // DB에서 모든 상품 조회

        // 상품 목록을 DTO로 변환
        return products.stream().map(product -> {
            Integer discountRate = (product.getProductInfo() != null && product.getProductInfo().getDiscountRate() != null)
                    ? product.getProductInfo().getDiscountRate()
                    : 0;

            return new ProductListDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getBrand().getBrandName(),
                    product.getProductType(),
                    product.getProductPrice(),
                    discountRate,
                    productImageRepository.findByProduct(product).stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList())
            );
        }).collect(Collectors.toList());

    }

    // 할인 상품만 조회
    public List<ProductListDTO> getDiscountedProducts() {
        List<Product> products = productRepository.findByDiscountRateGreaterThan(0);  // 할인율이 0보다 큰 상품 조회

        return products.stream().map(product -> {
            Integer discountRate = (product.getProductInfo() != null) ? product.getProductInfo().getDiscountRate() : 0;
            return new ProductListDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getBrand().getBrandName(),
                    product.getProductType(),
                    product.getProductPrice(),
                    discountRate,
                    productImageRepository.findByProduct(product).stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList())
            );
        }).collect(Collectors.toList());
    }

    // 선택한 상품 상세 페이지 조회
    public ProductDTO getProductDetailById(Integer productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        // 2. 상품 상세 정보 조회
        Optional<ProductInfo> optionalProductInfo = productInfoRepository.findByProduct(product);
        ProductInfo productInfo = optionalProductInfo.orElse(new ProductInfo()); // null 체크 후 사용


        // 3. 상품 성분 정보 조회
        List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(product);


        // 4. 성분 정보 DTO 변환
        List<IngredientWithInfoDTO> ingredientDetailDTOs = ingredientDetails.stream()
                .map(detail -> new IngredientWithInfoDTO(
                        detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : "Unknown",
                        detail.getIngredientAmount() != null ? detail.getIngredientAmount() : BigDecimal.ZERO,
                        detail.getIngredientUnit() != null ? detail.getIngredientUnit() : "N/A"
                ))
                .collect(Collectors.toList());

        // 5. 100g 기준 단백질 함량 계산
        Double proteinPer100g = 0.0;
        if (productInfo != null) {
            // ingredientsPer100g을 계산하고 단백질 정보를 가져오기
            Map<String, Double> ingredientPer100gMap = calculateIngredientsPer100g(productInfo);

            // "단백질" 성분이 맵에 존재하면, 해당 값을 proteinPer100g에 할당
//            if (ingredientPer100gMap.containsKey("단백질")) {
//                proteinPer100g = ingredientPer100gMap.get("단백질");
//            }
        }
        // 할인율과 할인가격 계산
        Integer discountRate = productInfo.getDiscountRate();
        BigDecimal productPrice = product.getProductPrice();
        BigDecimal discountedPrice = productPrice;

        if (discountRate != null && discountRate > 0) {
            BigDecimal discountAmount = productPrice.multiply(BigDecimal.valueOf(discountRate).divide(BigDecimal.valueOf(100)));
            discountedPrice = productPrice.subtract(discountAmount);  // 할인된 가격 계산
        }

        // 6. ProductDTO 생성 및 반환
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown")
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .discountRate(discountRate)  // 할인율 추가
                .discountedPrice(discountedPrice)  // 할인된 가격 추가
                .productImages(product.getProductImages().stream()
                        .map(image -> image.getImageUrl())
                        .collect(Collectors.toList()))
                .ingredientDTOs(ingredientDetailDTOs)
                .calories(productInfo.getCalories())  // 이미 null 체크가 되어 있으므로 직접 호출
                .servingSize(productInfo.getServingSize())  // 동일하게 null 처리 없이 사용
                .proteinAmount(productInfo.getProteinAmount())  // 단백질 함량
                .proteinPer100g(proteinPer100g)  // 계산된 100g 기준 단백질 함량 추가
                .productInfo(productInfo)  // productInfo 포함
                .build();

    }


    // 각 성분의 100g당 함량 계산
    public Map<String, Double> calculateIngredientsPer100g(ProductInfo productInfo) {
        // 1회 섭취량 (Serving Size) 가져오기
        Integer servingSize = productInfo.getServingSize();

        if (servingSize == null || servingSize <= 0) {
            return Collections.emptyMap(); // 섭취량이 없거나 잘못된 경우 빈 맵 반환
        }

        // 결과를 담을 맵 (성분명 -> 100g 기준 성분량)
        Map<String, Double> ingredientPer100gMap = new HashMap<>();

        // 2. 성분 정보 조회 (모든 성분에 대해 계산)
        List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(productInfo.getProduct());

        for (ProductIngredientDetail ingredientDetail : ingredientDetails) {
            String ingredientName = ingredientDetail.getIngredient().getIngredientName();
            BigDecimal ingredientAmount = ingredientDetail.getIngredientAmount();

            // 성분량이 없으면 무시
            if (ingredientAmount != null && ingredientAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 성분 단위를 안전하게 가져오기
                String ingredientUnit = ingredientDetail.getIngredientUnit();

                // IngredientWithInfoDTO 객체 생성
                IngredientWithInfoDTO ingredientDTO = new IngredientWithInfoDTO(
                        ingredientName, ingredientAmount, ingredientUnit);

                // 성분량 계산 (100g 기준)
                BigDecimal ingredientPer100g = ingredientDTO.calculatePer100g(servingSize);

                // 맵에 성분명과 100g 기준 함량을 추가
                ingredientPer100gMap.put(ingredientName, ingredientPer100g.doubleValue());
            }
        }

        // 3. 결과 반환
        return ingredientPer100gMap;
    }


    // 검색
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

            // ProductSearchDTO 생성
            ProductSearchDTO productSearchDTO = new ProductSearchDTO(
                    product.getProductName(),
                    product.getProductPrice(),
                    imageUrls,
                    product.getProductId(),
                    product.getBrand().getBrandName(),
                    product.getProductInfo().getDiscountRate(),  // 할인율
                    null // 할인된 가격은 null로 초기화
            );

            // 할인된 가격 계산
            productSearchDTO.calculateDiscountedPrice();

            return productSearchDTO;
        }).collect(Collectors.toList());
    }
}


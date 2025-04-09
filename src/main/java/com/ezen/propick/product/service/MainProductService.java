package com.ezen.propick.product.service;

import com.ezen.propick.bookmark.repository.BookmarkRepository;
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
    private final BookmarkRepository bookmarkRepository;


    // Product를 ProductListDTO로 변환하는 메서드
    public ProductListDTO convertToProductListDTO(Product product) {
        if (product == null) {
            return null;
        }

        // 할인율 가져오기 (ProductInfo에서 조회, 없으면 0)
        Integer discountRate = (product.getProductInfo() != null) ? product.getProductInfo().getDiscountRate() : 0;

        // 상품 이미지 URL 리스트 가져오기
        List<String> imageUrls = product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        // 카테고리 이름 리스트 가져오기
        List<String> categoryNames = product.getProductCategories() != null ?
                product.getProductCategories().stream()
                        .map(pc -> pc.getCategory().getCategoryName())
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        // ProductListDTO 생성
        return new ProductListDTO(
                product.getProductId(),
                product.getProductName(),
                product.getBrand() != null ? product.getBrand().getBrandName() : "브랜드 없음",
                product.getProductType(),
                product.getProductPrice(),
                discountRate,
                imageUrls,
                product.getProductCreatedAt(), // 등록 시간 추가
                categoryNames // 카테고리 이름 리스트 추가
        );
    }

    // 할인이 적용된 상품들 중 카테고리 별로 조회
    public List<ProductListDTO> getDiscountedCategoryProducts(Integer categoryId, Boolean discount) {

        List<Product> products;

        if (discount != null && discount) {
            if (categoryId != null) {
                // 할인 + 카테고리 상품 조회
                products = productRepository.findDiscountedProductsByCategoryId(categoryId);
            } else {
                // 할인 되는 상품 조회
                products = productRepository.findByDiscountRateGreaterThan(0);
            }
        } else {
            if (categoryId != null) {
                // 카테고리 해당 되는 상품 조회
                products = productRepository.findByCategoryId(categoryId);
            } else {
                // 전체 상품 조회
                products = productRepository.findAll();
            }
        }

        return products.stream().map(product -> {
            // 할인율 있으면 정보 가져오고 없으면 0 을 반환
            Integer discountRate = (product.getProductInfo() != null) ? product.getProductInfo().getDiscountRate() : 0;

            // 카테고리명 리스트 조회
            List<String> categoryNames = product.getProductCategories().stream()
                    .map(pc -> pc.getCategory().getCategoryName())
                    .collect(Collectors.toList());

            return new ProductListDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getBrand().getBrandName(),
                    product.getProductType(),
                    product.getProductPrice(),
                    discountRate,
                    productImageRepository.findByProduct(product).stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList()),
                    product.getProductCreatedAt(),
                    categoryNames
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


        // 4. 성분 정보 DTO 변환 (ingredientId 추가)
        List<IngredientWithInfoDTO> ingredientDetailDTOs = ingredientDetails.stream()
                .map(detail -> IngredientWithInfoDTO.builder()
                        .ingredientId(detail.getIngredient() != null ? detail.getIngredient().getIngredientId() : null)
                        .ingredientName(detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : "Unknown")
                        .ingredientAmount(detail.getIngredientAmount() != null ? detail.getIngredientAmount() : BigDecimal.ZERO)
                        .ingredientUnit(detail.getIngredientUnit() != null ? detail.getIngredientUnit() : "N/A")
                        .build())
                .collect(Collectors.toList());


        // 5. 할인율과 할인가격 계산
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
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "브랜드 이름 없음")
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
                //.proteinPer100g(proteinPer100g)  // 계산된 100g 기준 단백질 함량 추가
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
                IngredientWithInfoDTO ingredientDTO = IngredientWithInfoDTO.builder()
                        .ingredientId(ingredientDetail.getIngredient().getIngredientId())
                        .ingredientName(ingredientName)
                        .ingredientAmount(ingredientAmount)
                        .ingredientUnit(ingredientUnit)
                        .build();

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
//    public List<ProductSearchDTO> getProductBySearchKeyword(String keyword, Integer userId) {
//        // 상품명
//        List<Product> productsByName = productRepository.findByProductNameContaining(keyword);
//        // 브랜드명
//        List<Product> productsByBrand = productRepository.findByBrandNameContaining(keyword);
//        // 프로틴 유형
//        List<Product> productsByType = productRepository.findByProductTypeContaining(keyword);
//
//        // 성분명
//        List<Product> productByIngredient = productRepository.findByIngredientNameContaining(keyword);
//
//        Set<Product> mergedProducts = new HashSet<>();
//
//        mergedProducts.addAll(productsByName);
//        mergedProducts.addAll(productsByBrand);
//        mergedProducts.addAll(productsByType);
//        mergedProducts.addAll(productByIngredient);
//
//        return mergedProducts.stream().map(product -> {
//            List<String> imageUrls = product.getProductImages()
//                    .stream()
//                    .map(ProductImage::getImageUrl)
//                    .collect(Collectors.toList());
//
//            // 북마크 상태 조회
//            boolean isBookmarked = bookmarkRepository.existsByProductIdAndUserId(product.getProductId(), userId);
//
//            // ProductSearchDTO 생성
//            ProductSearchDTO productSearchDTO = new ProductSearchDTO(
//                    product.getProductId(),
//                    product.getProductName(),
//                    product.getProductPrice(),
//                    imageUrls,
//                    product.getBrand().getBrandName(),
//                    product.getProductInfo().getDiscountRate(),  // 할인율
//                    null, // 할인된 가격은 null로 초기화
//                    isBookmarked
//
//            );
//
//            // 할인된 가격 계산
//            productSearchDTO.calculateDiscountedPrice();
//
//            return productSearchDTO;
//        }).collect(Collectors.toList());
//    }
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

            // null 체크를 포함한 브랜드명과 할인율 가져오기
            String brandName = (product.getBrand() != null) ? product.getBrand().getBrandName() : "브랜드 없음";
            Integer discountRate = (product.getProductInfo() != null) ? product.getProductInfo().getDiscountRate() : 0;

            // ProductSearchDTO 생성 (8개 인수 모두 제공)
            ProductSearchDTO productSearchDTO = new ProductSearchDTO(
                    product.getProductId(),           // productId
                    product.getProductName(),         // productName
                    product.getProductPrice(),        // productPrice
                    imageUrls,                        // productImages
                    brandName,                        // brandName
                    discountRate,                     // discountRate
                    null,                             // discountedPrice (초기값 null)
                    false                             // bookmarked (기본값 false)
            );

            // 할인된 가격 계산
            productSearchDTO.calculateDiscountedPrice();

            return productSearchDTO;
        }).collect(Collectors.toList());
    }
}
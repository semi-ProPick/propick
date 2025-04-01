package com.ezen.propick.product.service;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.IngredientWithInfoDTO;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.ProductImageRepository;
import com.ezen.propick.product.repository.ProductInfoRepository;
import com.ezen.propick.product.repository.ProductIngredientDetailRepository;
import com.ezen.propick.product.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainProductService {

    private static final Logger logger = LoggerFactory.getLogger(MainProductService.class);

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductInfoRepository productInfoRepository;
    private final ProductIngredientDetailRepository productIngredientDetailRepository;
    private final BookmarkService bookmarkService;

    /* 전체 상품 목록 조회 */
    @Transactional
    public List<ProductListDTO> getAllProducts(Integer userNo) {
        logger.info("Fetching all products for userNo: {}", userNo);
        List<Product> products = productRepository.findAll();
        if (products == null || products.isEmpty()) {
            logger.warn("No products found in the database");
            return new ArrayList<>();
        }

        List<ProductListDTO> productList = products.stream().map(product -> {
            List<String> imageUrls = product.getProductImages() != null
                    ? product.getProductImages().stream()
                    .map(ProductImage::getImageUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            ProductListDTO dto = new ProductListDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown",
                    product.getProductType(),
                    product.getProductPrice(),
                    imageUrls
            );

            // 북마크 상태 설정
            if (userNo != null) {
                boolean isBookmarked = bookmarkService.isBookmarked(userNo, product.getProductId());
                dto.setIsBookmarked(isBookmarked);
            }

            return dto;
        }).collect(Collectors.toList());

        logger.info("Fetched and converted {} products to DTOs", productList.size());
        return productList;
    }

    @Transactional
    public List<ProductListDTO> getAllProducts() {
        return getAllProducts(null);
    }

    // 선택한 상품 상세 페이지 조회
    @Transactional
    public ProductDTO getProductDetailById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));
        Optional<ProductInfo> optionalProductInfo = productInfoRepository.findByProduct(product);
        ProductInfo productInfo = optionalProductInfo.orElse(new ProductInfo());
        List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(product);
        Map<String, String> nutrientsMap = new HashMap<>();
        if (productInfo.getNutrients() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                nutrientsMap = objectMapper.readValue(productInfo.getNutrients(), new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                logger.error("Error parsing nutrients for productId: {}", productId, e);
            }
        }
        List<IngredientWithInfoDTO> ingredientDetailDTOs = ingredientDetails.stream()
                .map(detail -> new IngredientWithInfoDTO(
                        detail.getIngredient() != null ? detail.getIngredient().getIngredientName() : "Unknown",
                        detail.getIngredientAmount() != null ? detail.getIngredientAmount() : BigDecimal.ZERO,
                        detail.getIngredientUnit() != null ? detail.getIngredientUnit() : "N/A"
                ))
                .collect(Collectors.toList());
        Double proteinPer100g = 0.0;
        if (productInfo != null) {
            proteinPer100g = calculateProteinPer100g(productInfo);
        }
        List<String> imageUrls = product.getProductImages() != null ? product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
        if (imageUrls.isEmpty()) {
            imageUrls = Collections.singletonList("");
        }
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown")
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .productImages(imageUrls)
                .nutrients(nutrientsMap)
                .ingredientDTOs(ingredientDetailDTOs)
                .calories(productInfo.getCalories())
                .servingSize(productInfo.getServingSize())
                .proteinAmount(productInfo.getProteinAmount())
                .proteinPer100g(proteinPer100g)
                .productInfo(productInfo)
                .build();
    }

    // 단백질 100g당 함량 계산
    public Double calculateProteinPer100g(ProductInfo productInfo) {
        if (productInfo.getServingSize() != null && productInfo.getProteinAmount() != null && productInfo.getServingSize() > 0) {
            return (double) productInfo.getProteinAmount() / productInfo.getServingSize() * 100;
        }
        return 0.0;
    }

    // 검색
    @Transactional
    public List<ProductSearchDTO> getProductBySearchKeyword(String keyword) {
        List<Product> productsByName = productRepository.findByProductNameContaining(keyword);
        List<Product> productsByBrand = productRepository.findByBrandNameContaining(keyword);
        List<Product> productsByType = productRepository.findByProductTypeContaining(keyword);
        List<Product> productByIngredient = productRepository.findByIngredientNameContaining(keyword);
        Set<Product> mergedProducts = new HashSet<>();
        mergedProducts.addAll(productsByName);
        mergedProducts.addAll(productsByBrand);
        mergedProducts.addAll(productsByType);
        mergedProducts.addAll(productByIngredient);
        return mergedProducts.stream().map(product -> {
            List<String> imageUrls = product.getProductImages() != null ? product.getProductImages()
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()) : new ArrayList<>();
            return new ProductSearchDTO(
                    product.getProductName(),
                    product.getProductPrice(),
                    imageUrls,
                    product.getProductId(),
                    product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown"
            );
        }).collect(Collectors.toList());
    }
}
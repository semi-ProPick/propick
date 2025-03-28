package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.IngredientWithInfoDTO;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.ProductImageRepository;
import com.ezen.propick.product.repository.ProductIngredientDetailRepository;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.product.utils.ImageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductIngredientDetailRepository productIngredientDetailRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<ProductListDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product ->
                new ProductListDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getBrand().getBrandName(),
                        product.getProductType(),
                        product.getProductPrice(),
                        productImageRepository.findByProduct(product).stream()
                                .map(ProductImage::getImageUrl)
                                .collect(Collectors.toList())
                )
        ).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductListDTO> getAllProductsWithCorrectedImages() {
        List<ProductListDTO> products = getAllProducts();
        products.forEach(product -> {
            List<String> correctedImageUrls = product.getProductImages().stream()
                    .map(imgUrl -> {
                        if (!imgUrl.startsWith("/images/product-img/")) {
                            return "/images/product-img/" + ImageUtils.decodeImageUrl(imgUrl);
                        }
                        return ImageUtils.decodeImageUrl(imgUrl);
                    })
                    .collect(Collectors.toList());
            product.setProductImages(correctedImageUrls);
        });
        return products;
    }

    @Transactional
    public ProductDTO getProductDetailById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        ProductInfo productInfo = product.getProductInfo();

        if (productInfo == null) {
            log.warn("⚠ No ProductInfo found for productId: {}", productId);
        } else {
            log.info("✅ ProductInfo found: {}", productInfo);
            log.info("Calories: {}", productInfo.getCalories());
            log.info("Serving Size: {}", productInfo.getServingSize());
            log.info("Discount Rate: {}", productInfo.getDiscountRate());
            log.info("Protein Amount: {}", productInfo.getProteinAmount());
        }

        List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(product);

        Map<String, String> nutrientsMap = new HashMap<>();
        if (productInfo != null && productInfo.getNutrients() != null) {
            try {
                nutrientsMap = objectMapper.readValue(productInfo.getNutrients(), new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                log.error("Failed to parse nutrients JSON for productId: {}", productId, e);
                nutrientsMap.put("error", "Invalid nutrients data");
            }
        }

        List<IngredientWithInfoDTO> ingredientDetailDTOs = ingredientDetails.stream()
                .map(detail -> {
                    String ingredientName = "Unknown";
                    BigDecimal ingredientAmount = BigDecimal.valueOf(0);
                    String ingredientUnit = "N/A";

                    try {
                        if (detail.getIngredient() != null) {
                            ingredientName = detail.getIngredient().getIngredientName();
                        }
                        if (detail.getIngredientAmount() != null) {
                            ingredientAmount = detail.getIngredientAmount();
                        }
                    } catch (Exception e) {
                        log.error("Failed to map ProductIngredientDetail for productId: {}, productIngredientId: {}",
                                productId, detail.getId(), e);
                    }

                    return new IngredientWithInfoDTO(ingredientName, ingredientAmount, ingredientUnit);
                })
                .collect(Collectors.<IngredientWithInfoDTO>toList()); // 타입 명시

        return ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brandName(product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown")
                .productType(product.getProductType())
                .productPrice(product.getProductPrice())
                .productImages(product.getProductImages().stream()
                        .map(image -> {
                            String imageUrl = image.getImageUrl();
                            if (!imageUrl.startsWith("/images/product-img/")) {
                                return "/images/product-img/" + ImageUtils.decodeImageUrl(imageUrl);
                            }
                            return ImageUtils.decodeImageUrl(imageUrl);
                        })
                        .collect(Collectors.toList()))
                .nutrients(nutrientsMap)
                .ingredientDTOs(ingredientDetailDTOs)
                .calories(productInfo != null ? productInfo.getCalories() : 0)
                .servingSize(productInfo != null ? productInfo.getServingSize() : 0)
                .discountRate(productInfo != null ? productInfo.getDiscountRate() : 0)
                .proteinAmount(productInfo != null ? productInfo.getProteinAmount() : 0)
                .build();
    }
}
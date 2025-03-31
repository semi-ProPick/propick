package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.IngredientWithInfoDTO;
import com.ezen.propick.product.dto.ProductCreateDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientDetailRepository productIngredientDetailRepository;
    private final ProductCategoryRepository productCategoryRepository;

    // 상품 목록 조회 (Pageable로 페이지네이션 처리)
    public Page<ProductListDTO> getAllProducts(Pageable pageable) {
        // DB에서 페이지네이션된 상품 조회
        Page<Product> products = productRepository.findAll(pageable);

        // 상품 목록을 DTO로 변환하여 반환
        return products.map(product -> {
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
        });
    }

    // 상품 등록
    @Transactional
    public void registerProduct(ProductCreateDTO productDTO) {
        // 1. 브랜드 찾기
        Brand brand = brandRepository.findById(productDTO.getBrandId())
                .orElseThrow(() -> new RuntimeException("브랜드를 찾을 수 없습니다."));

        // 2. 상품 엔티티 생성
        Product product = Product.builder()
                .productName(productDTO.getProductName())
                .productType(productDTO.getProductType())
                .productPrice(productDTO.getProductPrice())
                .brand(brand)  // 브랜드 연결
                .build();

        // 3. 상품 정보(ProductInfo) 생성
        ProductInfo productInfo = ProductInfo.builder()
                .discountRate(productDTO.getDiscountRate())  // 할인율
                .servingSize(productDTO.getServingSize())    // 1회 섭취량
                .calories(productDTO.getCalories())          // 칼로리
                .proteinAmount(productDTO.getProteinAmount()) // 단백질 함량
                .build();

        product.setProductInfo(productInfo); // Product와 ProductInfo 연결

        // 4. 카테고리 연결
        List<Category> categories = categoryRepository.findAllById(productDTO.getCategoryIds());
        if (categories.isEmpty()) {
            throw new RuntimeException("카테고리를 찾을 수 없습니다.");
        }

        // 5. ProductCategory로 관계 연결
        for (Category category : categories) {
            ProductCategory productCategory = ProductCategory.builder()
                    .product(product)
                    .category(category)
                    .build();

            productCategoryRepository.save(productCategory);  // ProductCategory 테이블에 저장
        }

        // 6. 상품 저장
        productRepository.save(product);

        // 7. 성분 정보 저장
        if (productDTO.getIngredients() != null) {
            for (IngredientWithInfoDTO ingredientDTO : productDTO.getIngredients()) {

                Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientDTO.getIngredientName())
                        .orElseThrow(() -> new RuntimeException("성분을 찾을 수 없습니다: " + ingredientDTO.getIngredientName()));


                ProductIngredientDetail productIngredientDetail = ProductIngredientDetail.builder()
                        .product(product)
                        .ingredient(ingredient)
                        .ingredientAmount(ingredientDTO.getIngredientAmount())
                        .build();

                productIngredientDetailRepository.save(productIngredientDetail);  // 성분 정보 저장
            }
        }

        // 8. 이미지 URL 저장
        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : productDTO.getImageUrls()) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imgUrl(imageUrl)
                        .build();

                productImageRepository.save(productImage);  // 이미지 URL 저장
            }
        }
    }

}

package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.*;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.*;
import com.ezen.propick.product.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ezen.propick.product.dto.ProductUpdateDTO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
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
    private final ProductInfoRepository productInfoRepository;

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
                            .collect(Collectors.toList()),
                    product.getProductCreatedAt()
            );
        });
    }

    // 검색
    public Page<ProductListDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);

        return productPage.map(product -> {
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
                            .collect(Collectors.toList()),
                    product.getProductCreatedAt()
            );
        });
    }

    // 상품 등록
    @Transactional
    public void registerProduct(ProductCreateDTO productDTO, List<MultipartFile> imageFiles) {
        Brand brand = brandRepository.findByBrandName(productDTO.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = Brand.builder()
                            .brandName(productDTO.getBrandName())
                            .build();
                    return brandRepository.save(newBrand);
                });

        Integer servingSize = productDTO.getServingSize() != null ? productDTO.getServingSize() : 1;
        Integer discountRate = productDTO.getDiscountRate() != null ? productDTO.getDiscountRate() : 0;

        Product product = Product.builder()
                .productName(productDTO.getProductName())
                .productType(productDTO.getProductType())
                .productPrice(productDTO.getProductPrice())
                .brand(brand)
                .build();
        productRepository.save(product);

        ProductInfo productInfo = ProductInfo.builder()
                .discountRate(discountRate)
                .servingSize(servingSize)
                .calories(productDTO.getCalories())
                .product(product)
                .build();
        productInfoRepository.save(productInfo);

        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(productDTO.getCategoryIds());
            if (categories.size() != productDTO.getCategoryIds().size()) {
                throw new IllegalArgumentException("일부 카테고리를 찾을 수 없습니다.");
            }
            List<ProductCategory> productCategories = categories.stream()
                    .map(category -> ProductCategory.builder()
                            .product(product)
                            .category(category)
                            .build())
                    .collect(Collectors.toList());
            productCategoryRepository.saveAll(productCategories);
        }

        // 성분 저장 (IngredientWithInfoDTO 사용)
        if (productDTO.getIngredientDTOs() != null && !productDTO.getIngredientDTOs().isEmpty()) {
            List<ProductIngredientDetail> productIngredientDetails = productDTO.getIngredientDTOs().stream()
                    .map(ingredientDTO -> {
                        if (ingredientDTO.getIngredientId() == null) {
                            throw new IllegalArgumentException("성분 ID는 필수 입력값입니다.");
                        }
                        // 성분 찾기 (db에서)
                        Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getIngredientId())
                                .orElseThrow(() -> new IllegalArgumentException("성분을 찾을 수 없습니다: " + ingredientDTO.getIngredientId()));

                        // 성분량 확인
                        BigDecimal amount = ingredientDTO.getIngredientAmount();
                        if (amount == null) {
                            throw new IllegalArgumentException("성분량은 필수 입력값입니다: " + ingredientDTO.getIngredientId());
                        }

                        return ProductIngredientDetail.builder()
                                .product(product)
                                .ingredient(ingredient)
                                .ingredientAmount(amount)
                                .ingredientUnit(ingredientDTO.getIngredientUnit())
                                .build();
                    })
                    .collect(Collectors.toList());
            productIngredientDetailRepository.saveAll(productIngredientDetails);
            productIngredientDetailRepository.flush();
        }


        // 이미지 저장
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<ProductImage> productImages = imageFiles.stream()
                    .map(imageFile -> {
                        try {
                            if (imageFile.isEmpty() || imageFile.getOriginalFilename() == null) {
                                throw new IllegalArgumentException("업로드된 파일이 비어 있거나 유효하지 않습니다.");
                            }
                            String originalFileName = imageFile.getOriginalFilename();
                            String imagePath = "/images/product-img/" + originalFileName;
                            String uploadDir = System.getProperty("user.dir") + "/uploads";
                            File imageFilePath = new File(uploadDir, imagePath);
                            File dir = imageFilePath.getParentFile();
                            if (!dir.exists() && !dir.mkdirs()) {
                                throw new IOException("디렉토리 생성 실패: " + dir.getAbsolutePath());
                            }
                            imageFile.transferTo(imageFilePath);
                            System.out.println("Saved file: " + imageFilePath.getAbsolutePath());
                            return ProductImage.builder()
                                    .product(product)
                                    .imgUrl(imagePath)
                                    .productImgName(originalFileName)
                                    .build();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException("이미지 파일 저장 실패", e);
                        }
                    })
                    .collect(Collectors.toList());
            productImageRepository.saveAll(productImages);
        }
    }

    // 상품 수정 할 데이터 조회
    public ProductUpdateDTO getProductUpdateById(Integer productId) {
       Product product = productRepository.findById(productId)
               .orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다." + productId));
       Optional<ProductInfo> optionProductInfo = productInfoRepository.findByProduct(product);
       ProductInfo productInfo = optionProductInfo.orElse(new ProductInfo());

       List<ProductIngredientDetail> ingredientDetails = productIngredientDetailRepository.findByProduct(product);
       List<ProductUpdateDTO.IngredientWithInfoDTO> ingredientDTOs = ingredientDetails.stream()
               .map(detail -> ProductUpdateDTO.IngredientWithInfoDTO.builder() // 수정
                       .ingredientId(detail.getIngredient().getIngredientId())
                       .ingredientName(detail.getIngredient().getIngredientName())
                       .ingredientAmount(detail.getIngredientAmount())
                       .ingredientUnit(detail.getIngredientUnit())
                       .build())
               .collect(Collectors.toList());

       return ProductUpdateDTO.builder()
               .productId(product.getProductId())
               .productName(product.getProductName())
               .brandName(product.getBrand() !=null ? product.getBrand().getBrandName() : "브랜드 이름 없음")
               .productType(product.getProductType())
               .productPrice(product.getProductPrice())
               .productImages(product.getProductImages().stream().map(ProductImage::getImageUrl)
                       .collect(Collectors.toList()))
               .ingredientDTOs(ingredientDTOs)
               .calories(productInfo.getCalories())
               .servingSize(productInfo.getServingSize())
               .discountRate(productInfo.getDiscountRate())
               .build();

    }

    // 상품 수정
    @Transactional
    public void updateProduct(Integer productId, ProductUpdateDTO productDTO,
                              List<MultipartFile> imageFiles,
                              List<Integer> deleteImgIds,
                              List<Integer> deleteIngredientIds) { // 삭제할 성분 ID 추가
        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));


        // 브랜드 조회 & 없으면 새로 생성
        Brand brand = brandRepository.findByBrandName(productDTO.getBrandName())
                .orElseGet(() -> brandRepository.save(Brand.builder()
                        .brandName(productDTO.getBrandName())
                        .build()));

        // 상품 정보 업데이트
        product.setProductName(productDTO.getProductName());
        product.setProductType(productDTO.getProductType());
        product.setBrand(brand);
        product.setProductPrice(productDTO.getProductPrice());
        productRepository.save(product);

        // ProductInfo 업데이트
        ProductInfo productInfo = productInfoRepository.findByProduct(product)
                .orElseGet(() -> new ProductInfo().setProduct(product));

        productInfo.setDiscountRate(productDTO.getDiscountRate() != null ? productDTO.getDiscountRate() : productInfo.getDiscountRate());
        productInfo.setServingSize(productDTO.getServingSize() != null ? productDTO.getServingSize() : productInfo.getServingSize());
        productInfo.setCalories(productDTO.getCalories());
        productInfoRepository.save(productInfo);

        // 성분 업데이트 (삭제하지 않고 유지 가능)
        updateProductIngredients(product, productDTO.getIngredientDTOs(),deleteIngredientIds);

        // 이미지 업데이트 (삭제하지 않고 유지 가능)
        updateProductImages(product, imageFiles, deleteImgIds);
    }


    private void updateProductIngredients(Product product, List<ProductUpdateDTO.IngredientWithInfoDTO> ingredientDTOs, List<Integer> deleteIngredientIds) {
        // 삭제할 성분 처리
        if (deleteIngredientIds != null && !deleteIngredientIds.isEmpty()) {
            List<ProductIngredientDetail> deleteList = productIngredientDetailRepository.findAllById(deleteIngredientIds);

            if (!deleteList.isEmpty()) {
                productIngredientDetailRepository.deleteAll(deleteList);
                System.out.println("✅ 성분 삭제 완료: " + deleteList.size() + "개 삭제됨");
            } else {
                System.out.println("⚠ 삭제할 성분이 없음");
            }
        }

        // 기존 성분 조회 (최종 성분 개수 확인)
        List<ProductIngredientDetail> existingIngredients = productIngredientDetailRepository.findByProduct(product);
        Map<Integer, ProductIngredientDetail> existingMap = existingIngredients.stream()
                .collect(Collectors.toMap(ProductIngredientDetail::getProductIngredientId, detail -> detail));


        // 새로 추가할 성분 처리
        List<ProductIngredientDetail> updatedIngredients = new ArrayList<>();
        if (ingredientDTOs != null && !ingredientDTOs.isEmpty()) {
            for (ProductUpdateDTO.IngredientWithInfoDTO dto : ingredientDTOs) {
                if (dto.getIngredientId() == null || dto.getIngredientAmount() == null) {
                    System.out.println("⚠ 필수 값 누락: " + dto);
                    continue;
                }

                Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                        .orElseThrow(() -> new IllegalArgumentException("성분을 찾을 수 없습니다: " + dto.getIngredientId()));

                ProductIngredientDetail detail = existingMap.getOrDefault(dto.getIngredientDetailId(), new ProductIngredientDetail());
                detail.setProduct(product);
                detail.setIngredient(ingredient);
                detail.setIngredientAmount(dto.getIngredientAmount());
                detail.setIngredientUnit(dto.getIngredientUnit());
                updatedIngredients.add(detail);
            }
            productIngredientDetailRepository.saveAll(updatedIngredients);
        }
        // 최종 성분 개수 확인
        System.out.println("📌 최종 성분 개수: " + productIngredientDetailRepository.findByProduct(product).size());

    }

    private void updateProductImages(Product product, List<MultipartFile> imageFiles, List<Integer> deleteImageIds) {
        // 이미지 삭제
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            productImageRepository.deleteAllById(deleteImageIds);
        }

        // 새 이미지 추가
        if (imageFiles != null && !imageFiles.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads";
            List<ProductImage> productImages = imageFiles.stream()
                    .filter(file -> !file.isEmpty() && file.getOriginalFilename() != null)
                    .map(imageFile -> {
                        try {
                            String originalFileName = imageFile.getOriginalFilename();
                            String imagePath = "/images/product-img/" + originalFileName;
                            File imageFilePath = new File(uploadDir, imagePath);
                            if (!imageFilePath.getParentFile().exists()) {
                                imageFilePath.getParentFile().mkdirs();
                            }
                            imageFile.transferTo(imageFilePath);
                            return ProductImage.builder()
                                    .product(product)
                                    .imgUrl(imagePath)
                                    .productImgName(originalFileName)
                                    .build();

                        } catch (IOException e) {
                            throw new RuntimeException("이미지 저장 실패", e);
                        }
                    })
                    .collect(Collectors.toList());
            productImageRepository.saveAll(productImages);
        }
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Integer productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);  // 상품 삭제
        } else {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }
    }
}


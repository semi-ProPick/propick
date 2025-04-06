package com.ezen.propick.product.service;

import com.ezen.propick.product.dto.*;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.*;
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

            // 카테고리명 리스트 추출
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
        });
    }

    // 검색
    public Page<ProductListDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);

        return productPage.map(product -> {
            Integer discountRate = (product.getProductInfo() != null) ? product.getProductInfo().getDiscountRate() : 0;

            // 카테고리명 리스트 추출
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
        });
    }

    // 상품 등록
    @Transactional
    public void registerProduct(ProductCreateDTO productDTO, List<MultipartFile> imageFiles) {

        // 등록되어 있는 브랜드를 찾거나 새로 등록
        Brand brand = brandRepository.findByBrandName(productDTO.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = Brand.builder()
                            .brandName(productDTO.getBrandName())
                            .build();
                    return brandRepository.save(newBrand);
                });

        Integer servingSize = productDTO.getServingSize() != null ? productDTO.getServingSize() : 1;
        Integer discountRate = productDTO.getDiscountRate() != null ? productDTO.getDiscountRate() : 0;

        // 상품 저장
        Product product = Product.builder()
                .productName(productDTO.getProductName())
                .productType(productDTO.getProductType())
                .productPrice(productDTO.getProductPrice())
                .brand(brand)
                .build();
        productRepository.save(product);

        // 상품 상세 정보 저장
        ProductInfo productInfo = ProductInfo.builder()
                .discountRate(discountRate)
                .servingSize(servingSize)
                .calories(productDTO.getCalories())
                .product(product)
                .build();
        productInfoRepository.save(productInfo);

        // 카테고리 저장
        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {

            // 중복 제거
            List<Integer> uniqueCategoryIds = productDTO.getCategoryIds().stream()
                    .distinct()  // 중복 제거
                    .collect(Collectors.toList());

            // 카테고리 조회
            List<Category> categories = categoryRepository.findAllById(uniqueCategoryIds);


            // 일부 카테고리를 찾지 못한 경우 예외 발생
            if (categories.size() != uniqueCategoryIds.size()) {
                throw new IllegalArgumentException(" 일부 카테고리를 찾을 수 없습니다. 요청한 ID: "
                        + uniqueCategoryIds + ", 찾은 ID: " + categories.stream().map(Category::getCategoryId).toList());
            }

            // ProductCategory 객체 생성 및 저장
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
                .map(detail -> new ProductUpdateDTO.IngredientWithInfoDTO(
                        detail.getIngredient().getIngredientId(),
                        detail.getIngredient().getIngredientName(),
                        detail.getIngredientUnit(),
                        detail.getIngredientAmount(),
                        detail.getProductIngredientId()  // 마지막 값 추가
                ))
                .collect(Collectors.toList());

       return ProductUpdateDTO.builder()
               .productId(product.getProductId())
               .productName(product.getProductName())
               .brandName(product.getBrand() !=null ? product.getBrand().getBrandName() : "브랜드 이름 없음")
               .productType(product.getProductType())
               .productPrice(product.getProductPrice())
               .productImages(
                       product.getProductImages().stream()
                               .map(image -> {
                                   ProductImageDTO dto = new ProductImageDTO();
                                   dto.setProductImageId(image.getProductImgId());
                                   dto.setProductImgUrl(image.getImageUrl());
                                   dto.setProductImgName(image.getProductImgName());
                                   return dto;
                               })
                               .collect(Collectors.toList())
               )
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

    // 성분 (추가,수정,삭제)
    private void updateProductIngredients(Product product, List<ProductUpdateDTO.IngredientWithInfoDTO> ingredientDTOs, List<Integer> deleteIngredientIds) {
        // 삭제
        if (deleteIngredientIds != null && !deleteIngredientIds.isEmpty()) {
            productIngredientDetailRepository.deleteAllById(deleteIngredientIds);
        }

        // 기존 성분 목록 가져오기
        List<ProductIngredientDetail> existing = productIngredientDetailRepository.findByProduct(product);
        Map<Integer, ProductIngredientDetail> existingMap = existing.stream()
                .collect(Collectors.toMap(ProductIngredientDetail::getProductIngredientId, detail -> detail));

        List<ProductIngredientDetail> toSave = new ArrayList<>();

        for (ProductUpdateDTO.IngredientWithInfoDTO dto : ingredientDTOs) {
            if (dto.getIngredientId() == null || dto.getIngredientAmount() == null) continue;

            ProductIngredientDetail detail;

            if (dto.getProductIngredientId() != null && existingMap.containsKey(dto.getProductIngredientId())) {
                // 기존 객체 업데이트
                detail = existingMap.get(dto.getProductIngredientId());
            } else {
                // 새 객체 생성
                detail = new ProductIngredientDetail();
                detail.setProduct(product);
                Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("성분 없음: " + dto.getIngredientId()));
                detail.setIngredient(ingredient);

            }

            // 수정값 덮어쓰기
            detail.setIngredientAmount(dto.getIngredientAmount());
            detail.setIngredientUnit(dto.getIngredientUnit());

            toSave.add(detail);
        }

        productIngredientDetailRepository.saveAll(toSave);
    }


    // 이미지 수정 (추가, 삭제)
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
    // 카테고리 수정(추가 ,삭제)
    @Transactional
    public void updateProductCategories(Integer productId, List<Integer> categoryIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("해당 상품이 존재하지 않습니다."));

        // 선택된 카테고리가 null or 비어 있으면 아무것도 안 함 (기존 유지)
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 기존 연결 제거
        productCategoryRepository.deleteByProduct(product);

        // 새로 선택된 카테고리 등록
        for (Integer categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("해당 카테고리가 존재하지 않습니다."));

            ProductCategory productCategory = new ProductCategory(product, category);
            productCategoryRepository.save(productCategory);
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


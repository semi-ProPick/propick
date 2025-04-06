package com.ezen.propick.product.controller;

import com.ezen.propick.product.dto.*;
import com.ezen.propick.product.entity.*;
import com.ezen.propick.product.repository.CategoryRepository;
import com.ezen.propick.product.repository.IngredientRepository;
import com.ezen.propick.product.repository.ProductCategoryRepository;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Profile("admin")
@Controller
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    // 상품 목록 조회 & 상품 검색
    @GetMapping("/products")
    public String listOrSearchProducts(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "desc") String sortOrder, // 기본값: 최신순
                                       Model model) {
        // 정렬 조건 설정 (등록일 기준 정렬)
        // 페이지네이션을 위한 Pageable 객체 생성
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by("productCreatedAt").ascending()
                : Sort.by("productCreatedAt").descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<ProductListDTO> productPage;

        // 검색어 여부에 따라 검색 또는 전체 조회 실행
        if (keyword != null && !keyword.isEmpty()) {
            productPage = adminProductService.searchProducts(keyword, pageable); // 검색 실행
        } else {
            productPage = adminProductService.getAllProducts(pageable); // 전체 조회 실행
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword); // 검색어 유지
        model.addAttribute("sortOrder", sortOrder);

        return "management/product";  // 상품 목록 페이지로 이동
    }

    // 상품 등록 폼
    @GetMapping("/products/regist")
    public String showProductForm(Model model) {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("productDTO", new ProductCreateDTO());
        model.addAttribute("ingredients", ingredients);  // 성분 목록을 모델에 추가
        model.addAttribute("categories", categories);
        return "management/product_regist";  // 상품 등록 폼 페이지로 이동
    }

    // 상품 등록 처리
    @PostMapping("/products/regist")
    public String createProduct(@ModelAttribute("productDTO") ProductCreateDTO productCreateDTO,
                                @RequestParam("imageFiles") List<MultipartFile> imageFiles) {      // 이미지 파일 추가

            // 상품 등록 처리
            adminProductService.registerProduct(productCreateDTO, imageFiles);
        return "redirect:/products";
    }


    // 상품 수정 폼
    @GetMapping("/products/edit/{productId}")
    public String showEditProductForm(@PathVariable Integer productId, Model model) {
        ProductUpdateDTO productUpdate = adminProductService.getProductUpdateById(productId);
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        // 이미지 URL만 DTO에 세팅
        List<ProductImageDTO> imageUrls = productUpdate.getProductImages();
        productUpdate.setProductImages(imageUrls);

        // 성분이 null이면 빈 리스트로 초기화
        if (productUpdate.getIngredientDTOs() == null) {
            productUpdate.setIngredientDTOs(new ArrayList<>());
        }

        // 상품에 등록된 카테고리 ID 리스트 가져오기
        List<Integer> selectedCategoryIds = productCategoryRepository.findCategoryIdsByProductId(productId);
        productUpdate.setCategoryIds(selectedCategoryIds);

        // 전체 카테고리 목록에서 선택된 것만 이름 추출
        List<String> selectedCategories = categories.stream()
                .filter(category -> selectedCategoryIds.contains(category.getCategoryId()))
                .map(Category::getCategoryName)
                .collect(Collectors.toList());

        // 모델에 세팅
        model.addAttribute("ProductUpdateDTO", productUpdate);
        model.addAttribute("ingredients", ingredients);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategories", selectedCategories);
        model.addAttribute("allCategories", categories); // allCategories = categories 동일함

        return "management/edit_product";  // 상품 수정 폼 페이지로 이동
    }



    // 상품 수정 처리
    @PostMapping("/products/edit/{productId}")
    public String updateProduct(@PathVariable Integer productId,
                                @ModelAttribute ProductUpdateDTO productUpdate,
                                @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                @RequestParam(value = "deleteImgIds", required = false) List<Integer> deleteImgIds,
                                @RequestParam(value = "deleteIngredientIds", required = false) List<Integer> deleteIngredientIds) {

        // 1. 상품 수정
        adminProductService.updateProduct(productId, productUpdate, imageFiles, deleteImgIds, deleteIngredientIds);

        // 2. 카테고리 수정
        adminProductService.updateProductCategories(productId, productUpdate.getCategoryIds());

        return "redirect:/products";
    }


    // 상품 삭제 처리
    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam("productId") Integer productId) {
        try {
            adminProductService.deleteProduct(productId);  // 서비스에서 삭제 처리
            return "redirect:/products";  // 삭제 후 상품 목록 페이지로 리다이렉트
        } catch (Exception e) {
            return "에러 발생";  // 삭제 중 에러 발생 시 에러 페이지로 리다이렉트
        }
    }

}

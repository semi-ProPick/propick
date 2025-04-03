package com.ezen.propick.product.controller;

import com.ezen.propick.product.dto.ProductCreateDTO;
import com.ezen.propick.product.dto.ProductIngredientDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductUpdateDTO;
import com.ezen.propick.product.entity.Category;
import com.ezen.propick.product.entity.Ingredient;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.entity.ProductImage;
import com.ezen.propick.product.repository.CategoryRepository;
import com.ezen.propick.product.repository.IngredientRepository;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
            productPage = adminProductService.searchProducts(keyword, pageable); // 🔍 검색 실행
        } else {
            productPage = adminProductService.getAllProducts(pageable); // 📋 전체 조회 실행
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword); // 검색어 유지
        model.addAttribute("sortOrder", sortOrder);

        return "management/product";  // 상품 목록 페이지로 이동
    }


//    @GetMapping("/products")
//    public String listProducts(@RequestParam(defaultValue = "0") int page, Model model) {
//        // 페이지네이션을 위한 Pageable 객체 생성
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("productId").ascending());
//
//        // 페이지네이션된 상품 목록을 DTO로 변환하여 반환
//        Page<ProductListDTO> productPage = adminProductService.getAllProducts(pageable);
//
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//
//        return "management/product";  // 상품 목록 페이지로 이동
//    }

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
        System.out.println("Ingredients: " + productCreateDTO.getIngredientDTOs());
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

        // ProductImage에서 이미지 URL만 추출해서 DTO에 추가
        List<String> imageUrls = productUpdate.getProductImages(); //
        productUpdate.setProductImages(imageUrls);

        // ingredientDTOs가 null이면 빈 리스트로 초기화
        if (productUpdate.getIngredientDTOs() == null) {
            productUpdate.setIngredientDTOs(new ArrayList<>());
        }

        model.addAttribute("ProductUpdateDTO", productUpdate);
        model.addAttribute("ingredients", ingredients);
        model.addAttribute("categories", categories);

        return "management/edit_product";  // 상품 수정 폼 페이지로 이동
    }

    // 상품 수정 처리
    @PostMapping("/products/edit/{productId}")
    public String updateProduct(@PathVariable Integer productId,   // 수정할 상품 아이디
                                @ModelAttribute ProductUpdateDTO productUpdate,  // 상품 수정용 dto
                                @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,  // 이미지
                                @RequestParam(value = "deleteImgIds" ,required = false) List<Integer> deleteImgIds,  // 수정, 삭제할때의 이미지 아이디
                                @RequestParam(value = "deleteIngredientIds",required = false) List<Integer> deleteIngredientIds ) { // 수정,삭제할 떄의 성분 아이디

        System.out.println("삭제할 이미지 ID 리스트: " + deleteImgIds);
        System.out.println("삭제할 성분 ID 리스트: " + deleteIngredientIds);

        adminProductService.updateProduct(productId,productUpdate,imageFiles,deleteImgIds,deleteIngredientIds);
        return "redirect:/products";  // 상품 목록 페이지로 리다이렉트
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

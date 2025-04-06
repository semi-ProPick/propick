package com.ezen.propick.product.controller;

import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("user")
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final MainProductService mainProductService;

    // 상품 목록 페이지 요청
    @GetMapping("/products")
    public String getAllProducts(@RequestParam(value = "category", required = false) Integer categoryId,
                                 @RequestParam(value = "discount", required = false) Boolean discount,
                                 Model model) {

        // 조건에 따라 필터링된 상품 조회
        List<ProductListDTO> products = mainProductService.getDiscountedCategoryProducts(categoryId, discount);

        // 각 상품의 이미지 URL을 디코딩하고, /images/product-img/로 경로 수정
        products.forEach(product -> {
            List<String> correctedImageUrls = product.getProductImages().stream()
                    .map(imgUrl -> {
                        // 만약 imgUrl이 /images/product-img/로 시작하지 않으면 추가
                        if (!imgUrl.startsWith("/images/product-img/")) {
                            return "/images/product-img/" + ImageUtils.decodeImageUrl(imgUrl);
                        }
                        return ImageUtils.decodeImageUrl(imgUrl);  // 이미 경로가 제대로 설정되어 있으면 그대로 사용
                    })
                    .collect(Collectors.toList());
            product.setProductImages(correctedImageUrls);  // 디코딩된 이미지 경로를 설정
        });
        model.addAttribute("products", products);  // 상품 목록을 모델에 추가
        model.addAttribute("discount", discount);  // 할인되는 정보 추가
        model.addAttribute("categoryId", categoryId); // 카테고리 아이디 추가
        return "main/product";  // 상품 목록 페이지 반환 (HTML 뷰)
    }


    // 각 상품 클릭시 상세 페이지로 이동 시 요청
    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        // 상품 상세 정보를 조회
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);

        // 성분별 100g당 함량 계산
        Map<String, Double> ingredientsPer100g = mainProductService.calculateIngredientsPer100g(productDetail.getProductInfo());

        // Model에 product와 proteinPer100g을 추가
        model.addAttribute("product", productDetail);
        model.addAttribute("ingredientsPer100g", ingredientsPer100g);

        // 상세 페이지로 전달
        return "main/product_detail";
    }

    // 검색창에 입력하고 검색 버튼 눌렀을 때 요청
    @GetMapping("/products/search")
    public String getProductName(@RequestParam(value = "keyword", required = false) String keyword,  // 검색어가 없어도 요청 가능 (required = false)
                                 Model model) { // 뷰에 데이터를 넘겨주기 위해 모델 사용

        // 검색 결과를 담을 DTO 리스트 선언
        List<ProductSearchDTO> products;

        // 검색어가 없거나 공백만 있는 경우 → 결과는 빈 리스트로 초기화
        if (keyword == null || keyword.trim().isEmpty()) {
            products = new ArrayList<>();
        } else {  // 검색어가 존재하면 → 서비스 메서드 getProductBySearchKeyword() 호출해서 결과 조회
            products = mainProductService.getProductBySearchKeyword(keyword); // 검색
        }

        model.addAttribute("products", products);  //검색 결과를 모델에 담아서 뷰에 전달
        return "main/product"; // 검색 결과를 main/product 페이지로 전달
    }

}
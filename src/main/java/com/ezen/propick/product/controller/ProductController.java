package com.ezen.propick.product.controller;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BookmarkService bookmarkService;


    // 상품 목록 페이지
    @GetMapping("/products")
    public String getAllProducts(@RequestParam(value = "discount", required = false) Boolean discount,Model model) {
        List<ProductListDTO> products;  // 전체 상품 조회

        if (discount != null && discount) {
            // 할인된 상품만 조회
            products = mainProductService.getDiscountedProducts();
        } else {
            // 전체 상품 조회
            products = mainProductService.getAllProducts();
        }

        // 각 상품의 이미지 URL을 디코딩하고, /images/product-img/로 경로 수정
        products.forEach(product -> {
            List<String> correctedImageUrls = product.getProductImages().stream()
                    .map(imgUrl -> {
                        // 만약 imgUrl이 이미 /images/product-img/로 시작하지 않으면 추가
                        if (!imgUrl.startsWith("/images/product-img/")) {
                            return "/images/product-img/" + ImageUtils.decodeImageUrl(imgUrl);
                        }
                        return ImageUtils.decodeImageUrl(imgUrl);  // 이미 경로가 제대로 설정되어 있으면 그대로 사용
                    })
                    .collect(Collectors.toList());
            product.setProductImages(correctedImageUrls);  // 디코딩된 이미지 경로를 설정
        });
        model.addAttribute("products", products);  // 상품 목록을 모델에 추가
        model.addAttribute("discount", discount);

        List<ProductListDTO> top3Products = bookmarkService.getTop3BookmarkedProducts();
        model.addAttribute("top3Products", top3Products);

        model.addAttribute("products", products);
        model.addAttribute("discount", discount);

        return "main/product";  // 상품 목록 페이지 반환 (HTML 뷰)
    }


    // 상품 상세 페이지
    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        // 1. 상품 상세 정보를 조회
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);

        // 2. 성분별 100g당 함량 계산
        Map<String, Double> ingredientsPer100g = mainProductService.calculateIngredientsPer100g(productDetail.getProductInfo());

        // 3. Model에 product와 proteinPer100g을 추가
        model.addAttribute("product", productDetail);
        model.addAttribute("ingredientsPer100g", ingredientsPer100g);

        // 4. 상세 페이지로 전달
        return "main/product_detail";
    }

    // 검색
    @GetMapping("/products/search")
    public String getProductName(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ProductSearchDTO> products;

        if (keyword == null || keyword.trim().isEmpty()) {
            products = new ArrayList<>();
        } else {
            products = mainProductService.getProductBySearchKeyword(keyword); // 검색
        }

        model.addAttribute("products", products);
        return "main/product"; // 검색 결과를 main/product 페이지로 전달
    }




}
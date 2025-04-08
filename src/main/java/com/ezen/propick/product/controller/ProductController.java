package com.ezen.propick.product.controller;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // 추가
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
                        if (!imgUrl.startsWith("/images/product-img/")) {
                            return "/images/product-img/" + ImageUtils.decodeImageUrl(imgUrl);
                        }
                        return ImageUtils.decodeImageUrl(imgUrl);
                    })
                    .collect(Collectors.toList());
            product.setProductImages(correctedImageUrls);
        });
        // 북마크 상태 반영
        String userId = getCurrentUserId();
        products = bookmarkService.getProductsWithBookmarkStatus(userId, products);

        // Top 3 북마크 상품 조회
        List<ProductListDTO> top3Products = bookmarkService.getTop3BookmarkedProducts();
        top3Products.forEach(product -> {
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

        // 북마크 상태 반영 (top3Products)
        top3Products = bookmarkService.getProductsWithBookmarkStatus(userId, top3Products);
        model.addAttribute("products", products);
        model.addAttribute("discount", discount);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("top3Products", top3Products); // top3Products를 모델에 추가
        return "main/product";
    }

    // 각 상품 클릭 시 상세 페이지로 이동
    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);
        Map<String, Double> ingredientsPer100g = mainProductService.calculateIngredientsPer100g(productDetail.getProductInfo());
        model.addAttribute("product", productDetail);
        model.addAttribute("ingredientsPer100g", ingredientsPer100g);
        return "main/product_detail";
    }

    // 검색창에서 검색 요청
    @GetMapping("/products/search")
    public String getProductName(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ProductSearchDTO> products;
        if (keyword == null || keyword.trim().isEmpty()) {
            products = new ArrayList<>();
        } else {
            products = mainProductService.getProductBySearchKeyword(keyword);
        }
        model.addAttribute("products", products);
        return "main/product";
    }

    @GetMapping("/products/top3")
    @ResponseBody
    @Transactional(readOnly = true) // 트랜잭션 추가
    public List<ProductListDTO> getTop3Products() {
        String userId = getCurrentUserId();
        List<ProductListDTO> top3Products = bookmarkService.getTop3BookmarkedProducts();

        // 이미지 URL 디코딩 및 경로 수정
        top3Products.forEach(product -> {
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

        // 북마크 상태 반영
        top3Products = bookmarkService.getProductsWithBookmarkStatus(userId, top3Products);
        System.out.println("Top 3 products returned: " + top3Products); // 디버깅 로그
        return top3Products;
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return null;
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}
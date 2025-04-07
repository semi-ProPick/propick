package com.ezen.propick.product.controller;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.entity.Ingredient;
import com.ezen.propick.product.entity.ProductIngredientDetail;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("user")
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final MainProductService mainProductService;
    private final BookmarkService bookmarkService;

    // 상품 목록 페이지
    @GetMapping("/products")
    public String getAllProducts(@RequestParam(value = "discount", required = false) Boolean discount, Model model) {
        List<ProductListDTO> products;

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
        model.addAttribute("top3Products", top3Products);
        model.addAttribute("discount", discount != null && discount);

        return "main/product";
    }

    // 상품 상세 페이지
    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);
        Map<String, BigDecimal> ingredientsPer100g = mainProductService.calculateIngredientsPer100g(
                productDetail.getProductInfo(),
                productDetail.getProductIngredients().stream()
                        .map(dto -> {
                            ProductIngredientDetail detail = new ProductIngredientDetail();
                            Ingredient ingredient = new Ingredient();
                            ingredient.setIngredientId(dto.getIngredientId());
                            // ingredientName은 DTO에 없으므로 설정하지 않음
                            detail.setIngredient(ingredient);
                            detail.setIngredientAmount(dto.getIngredientAmount() != null ? BigDecimal.valueOf(dto.getIngredientAmount()) : null);
                            detail.setIngredientUnit(dto.getIngredientUnit());
                            return detail;
                        })
                        .collect(Collectors.toList())
        );

        model.addAttribute("product", productDetail);
        model.addAttribute("ingredientsPer100g", ingredientsPer100g);

        return "main/product_detail";
    }

    // 검색
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

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
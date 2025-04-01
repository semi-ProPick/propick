package com.ezen.propick.product.controller;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.dto.ProductSearchDTO;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import com.ezen.propick.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final MainProductService mainProductService;
    private final BookmarkService bookmarkService;

    private Integer getCurrentUserNo() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getUserNo();
            }
            log.warn("No authenticated user found, returning null userNo");
        } catch (Exception e) {
            log.error("Error retrieving current userNo: {}", e.getMessage(), e);
        }
        return null;
    }

    @GetMapping("/products")
    public String getAllProducts(Model model, HttpServletRequest request) { // HttpServletRequest 추가
        Integer userNo = getCurrentUserNo();
        if (userNo == null) {
            log.warn("User is not authenticated, bookmark status will not be set");
        }

        List<ProductListDTO> products = mainProductService.getAllProducts(userNo);
        if (products == null) {
            log.warn("mainProductService.getAllProducts(userNo) returned null!");
            products = new ArrayList<>();
        }

        log.info("Number of products retrieved: {}", products.size());

        products.forEach(product -> {
            log.info("Product: {} - Price: {}", product.getProductName(), product.getProductPrice());
            List<String> correctedImageUrls = new ArrayList<>();
            if (product.getProductImages() != null) {
                for (String imgUrl : product.getProductImages()) {
                    if (imgUrl == null) {
                        correctedImageUrls.add("");
                    } else {
                        String decodedUrl = ImageUtils.decodeImageUrl(imgUrl);
                        if (!decodedUrl.startsWith("/images/product-img/")) {
                            decodedUrl = "/images/product-img/" + decodedUrl;
                        }
                        correctedImageUrls.add(decodedUrl);
                    }
                }
            }
            product.setProductImages(correctedImageUrls);
            product.setIsBookmarked(userNo != null && bookmarkService.isBookmarked(userNo, product.getProductId()));
        });
        model.addAttribute("products", products);
        model.addAttribute("contextPath", request.getContextPath()); // contextPath 추가
        return "main/product";
    }

    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model, HttpServletRequest request) { // HttpServletRequest 추가
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);
        if (productDetail == null) {
            log.error("Product with id {} not found", productId);
            return "error/404";
        }
        List<String> imageUrls = productDetail.getProductImages();
        List<String> correctedImageUrls = new ArrayList<>();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imgUrl : imageUrls) {
                if (imgUrl == null) {
                    correctedImageUrls.add("");
                } else {
                    String decodedUrl = ImageUtils.decodeImageUrl(imgUrl);
                    if (decodedUrl == null || decodedUrl.isEmpty()) {
                        decodedUrl = "/images/default.png";
                    }
                    if (!decodedUrl.startsWith("/images/product-img/")) {
                        decodedUrl = "/images/product-img/" + decodedUrl;
                    }
                    correctedImageUrls.add(decodedUrl);
                }
            }
        } else {
            log.warn("Product {} has no images", productId);
        }
        productDetail.setProductImages(correctedImageUrls);
        model.addAttribute("product", productDetail);
        model.addAttribute("proteinPer100g", productDetail.getProteinPer100g());
        model.addAttribute("contextPath", request.getContextPath()); // contextPath 추가
        return "main/product_detail";
    }

    @GetMapping("/products/search")
    public String getProductName(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model, HttpServletRequest request) { // HttpServletRequest 추가
        Integer userNo = getCurrentUserNo();
        if (userNo == null) {
            log.warn("User is not authenticated, bookmark status will not be set");
        }

        List<ProductSearchDTO> searchResults;
        if (keyword == null || keyword.trim().isEmpty()) {
            searchResults = new ArrayList<>();
        } else {
            searchResults = mainProductService.getProductBySearchKeyword(keyword);
        }
        List<ProductListDTO> products = searchResults.stream()
                .map(p -> new ProductListDTO(
                        p.getProductId(),
                        p.getProductName(),
                        p.getBrandName(),
                        "",
                        p.getProductPrice(),
                        p.getProductImages()
                ))
                .collect(Collectors.toList());
        if (products == null) {
            products = new ArrayList<>();
        }
        products.forEach(product -> {
            product.setIsBookmarked(userNo != null && bookmarkService.isBookmarked(userNo, product.getProductId()));
        });
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("contextPath", request.getContextPath()); // contextPath 추가
        return "main/product";
    }
}
package com.ezen.propick.product.controller;

import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.product.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final MainProductService mainProductService;


    // 상품 목록 페이지
    @GetMapping("/products")
    public String getAllProducts(Model model) {
        List<ProductListDTO> products = mainProductService.getAllProducts();  // 전체 상품 조회
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
        return "main/product";  // 상품 목록 페이지 반환 (HTML 뷰)
    }


     // 상품 상세 페이지
    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        ProductDTO productDetail = mainProductService.getProductDetailById(productId);
        model.addAttribute("product", productDetail);
        return "main/product_detail";

    }



}

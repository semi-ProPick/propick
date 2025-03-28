package com.ezen.propick.product.controller;

import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.service.MainProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final MainProductService mainProductService;

    @GetMapping("/products")
    public String getAllProducts(Model model) {
        try {
            List<ProductListDTO> products = mainProductService.getAllProductsWithCorrectedImages();
            model.addAttribute("products", products);
            return "main/product";
        } catch (Exception e) {
            model.addAttribute("error", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            return "error";
        }
    }

    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        try {
            ProductDTO productDetail = mainProductService.getProductDetailById(productId);
            model.addAttribute("product", productDetail);
            return "main/product_detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
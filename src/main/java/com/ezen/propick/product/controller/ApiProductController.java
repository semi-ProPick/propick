package com.ezen.propick.product.controller;

import com.ezen.propick.auth.AuthDetails;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.service.MainProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ApiProductController {

    private final MainProductService mainProductService;

    public ApiProductController(MainProductService mainProductService) {
        this.mainProductService = mainProductService;
    }

    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getAllProducts() {
        Integer userNo = getCurrentUserNo(); // 인증된 사용자 번호 (필요 시)
        List<ProductListDTO> products = mainProductService.getAllProducts(userNo);
        return ResponseEntity.ok(products);
    }

    private Integer getCurrentUserNo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof AuthDetails) {
                return ((AuthDetails) principal).getUserNo();
            }
        }
        return null;
    }
}
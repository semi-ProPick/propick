package com.ezen.propick.product.controller;

import com.ezen.propick.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminProductController {
    private final AdminProductService adminProductService;

//    @GetMapping('/managemnet/products')
//    public String
}

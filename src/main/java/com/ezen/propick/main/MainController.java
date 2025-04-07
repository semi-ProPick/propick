package com.ezen.propick.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String mainPage() {
        return "main/main";
    }

    @GetMapping("/brandIntro")
    public String brandstroyPage() {
        return "main/brandIntro";
    }

    @GetMapping("/collabo")
    public String collaboPage() {
        return "main/collabo";
    }
}

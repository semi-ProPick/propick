package com.ezen.propick;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController {
    @GetMapping
    public String main() {
        return "/main/main";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "/main/mypage";
    }
}

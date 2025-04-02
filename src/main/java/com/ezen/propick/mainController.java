package com.ezen.propick;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Profile("User")
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

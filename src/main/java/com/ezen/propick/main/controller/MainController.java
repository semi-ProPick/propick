package com.ezen.propick.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage() {
        return "main/main"; // templates/main.html을 의미
    }

    @GetMapping("/mypage")
    public String mypage() {
            return "/main/mypage";
        }

}

package com.ezen.propick.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class PageController {

    @GetMapping
    public String main() {
        return "main/main";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "main/mypage";
    }

    @GetMapping("/product")
    public String product() {
        return "main/product";
    }

    @GetMapping("/brandIntro")
    public String brandIntro() {
        return "main/brandIntro";
    }

    @GetMapping("/event")
    public String event() {
        return "main/event";
    }

    @GetMapping("/announcement")
    public String announcement() {
        return "main/announcement";
    }

    @GetMapping("/fqBoard")
    public String fqBoard() {
        return "main/fqBoard";
    }

    @GetMapping("/qaBoard")
    public String qaBoard() {
        return "main/qaBoard";
    }

    @GetMapping("/freeBoard")
    public String freeBoard() {
        return "main/freeBoard";
    }

    @GetMapping("/login")
    public String login() {
        return "main/login";
    }

    @GetMapping("/recommend")
    public String recommend() {
        return "main/recommend";
    }
}
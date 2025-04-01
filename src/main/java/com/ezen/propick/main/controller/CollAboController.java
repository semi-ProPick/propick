package com.ezen.propick.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CollAboController {

    @GetMapping("/collabo")
    public String mainPage() {
        return "main/collabo"; // templates/main.html을 의미
    }
}

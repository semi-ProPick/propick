package com.ezen.propick.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QaBoardController {

    @GetMapping("/qaboard")

    public String mainPage() {
        return "main/qaboard";
    }
}

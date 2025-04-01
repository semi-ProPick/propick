package com.ezen.propick.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FreeBoardController {

    @GetMapping("/free_board")

    public String mainPage() {
        return "main/free_board";
    }
}

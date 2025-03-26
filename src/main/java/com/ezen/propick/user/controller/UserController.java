package com.ezen.propick.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/mypage")
    public String showMypage() {
        return ("main/mypage");
    }
}

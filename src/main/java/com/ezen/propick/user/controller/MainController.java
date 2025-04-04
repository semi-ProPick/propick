package com.ezen.propick.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("user")
@Controller
@RequiredArgsConstructor
public class MainController {
    @GetMapping
    public String index(Model model) {
        return "/main/main";
    }


}

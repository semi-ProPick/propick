package com.ezen.propick.bookmark.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("admin")
@Controller
public class AdminController {

    @GetMapping("/index")
    public String AdminController() {
            return "/management/index";
    }
}

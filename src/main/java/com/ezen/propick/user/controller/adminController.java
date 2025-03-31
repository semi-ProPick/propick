package com.ezen.propick.user.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("admin")
@Controller
public class adminController {
    @GetMapping
    public String admin(){
        return "management/index";
    }
}

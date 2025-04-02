package com.ezen.propick.user.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Profile("admin")
@Controller
public class adminController {
    @GetMapping
    public String admin(){
        return "management/user";
    }



}

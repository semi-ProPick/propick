package com.ezen.propick.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventController {

    @GetMapping("/event")
    public String mainPage() {
        return "main/event";
    }
}

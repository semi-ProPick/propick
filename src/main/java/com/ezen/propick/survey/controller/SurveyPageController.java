package com.ezen.propick.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SurveyPageController {

    @GetMapping("/survey")
    public String openSurveyPage() {
        return "main/survey";
    }
}

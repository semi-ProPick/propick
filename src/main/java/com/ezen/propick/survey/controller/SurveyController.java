package com.ezen.propick.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SurveyController {

    @GetMapping("/survey_start")
    public String showSurveyPage() {
        return "main/survey";
    }


    @GetMapping("/main")
    public String showMainPage() {
        return "main/main";  // 설문 종료 시 메인 페이지로 이동
    }

}

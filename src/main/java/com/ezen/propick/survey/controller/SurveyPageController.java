package com.ezen.propick.survey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SurveyPageController {

    //설문 시작 페이지
    @GetMapping("/survey_start")
    public String openSurveyStartPage() {
        return "main/survey_start";
    }

    //설문 페이지
    @GetMapping("/survey")
    public String openSurveyPage() {
        return "main/survey";
    }

    // 설문 결과 페이지
    @GetMapping("/survey_result")
    public String openResultPage() {
        return "main/survey_result";
    }

    //설문 마이페이지
    @GetMapping("/survey_mypage")
    public String openSurveyMyPage() {
        return "main/survey_mypage";
    }
}

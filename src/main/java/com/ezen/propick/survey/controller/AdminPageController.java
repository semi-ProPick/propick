package com.ezen.propick.survey.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("admin")
@Controller
public class AdminPageController {
    @GetMapping("/admin/survey_modify")
    public String openSurveyStartPage() {
        return "management/survey_modify";
    }

}

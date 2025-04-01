package com.ezen.propick.admin.controller;

import com.ezen.propick.survey.dto.recommendation.SatisfactionDTO;
import com.ezen.propick.survey.service.SatisfactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminSatisfactionController {

    private final SatisfactionService satisfactionService;

    @GetMapping("/satisfaction")
    public String viewAllSatisfaction(Model model) {
        List<SatisfactionDTO> satisfactionList = satisfactionService.getAllSatisfaction(); // DTO 리스트로 받기
        model.addAttribute("satisfactionList", satisfactionList);
        return "admin/satisfaction_list"; // templates/admin/satisfaction_list.html로 이동
    }
}

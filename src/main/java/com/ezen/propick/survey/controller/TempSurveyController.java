package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class TempSurveyController {

    @PostMapping("/temp-survey")
    public ResponseEntity<Void> saveTempSurvey(
            @RequestBody SurveyResponseRequestDTO dto,
            HttpSession session
    ) {
        session.setAttribute("tempSurvey", dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/temp-survey")
    public ResponseEntity<SurveyResponseRequestDTO> getTempSurvey(HttpSession session) {
        SurveyResponseRequestDTO dto = (SurveyResponseRequestDTO) session.getAttribute("tempSurvey");
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
}

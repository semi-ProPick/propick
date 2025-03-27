package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyDTO;
import com.ezen.propick.survey.entity.Survey;
import com.ezen.propick.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Integer surveyId) {
        SurveyDTO surveyDTO = surveyService.getSurveyById(surveyId);
        return ResponseEntity.ok(surveyDTO);

    }
}

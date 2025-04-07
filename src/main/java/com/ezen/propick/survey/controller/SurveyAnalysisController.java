package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import com.ezen.propick.survey.service.SurveyAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/survey-analysis")
@RequiredArgsConstructor
public class SurveyAnalysisController {

    private final ProteinRecommendationEngine recommendationEngine;
    private final SurveyAnalysisService surveyAnalysisService;

    @PostMapping("/{responseId}")
    public ResponseEntity<SurveyRecommendationResultDTO> analyzeSurvey(@PathVariable Integer responseId) {
        return ResponseEntity.ok(surveyAnalysisService.analyzeSurvey(responseId));
    }

}


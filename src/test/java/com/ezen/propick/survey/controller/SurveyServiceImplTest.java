package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyDTO;
import com.ezen.propick.survey.service.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest // SpringBoot 테스트 환경 로딩
public class SurveyServiceImplTest {

    @Autowired
    private SurveyService surveyService;

    @Test
    @DisplayName("surveyDTO 호출")
    public void testGetSurveyById() {
        Integer surveyId = 1; // 테스트할 설문 ID

        SurveyDTO surveyDTO = surveyService.getSurveyById(surveyId);

        System.out.println(surveyDTO);
    }
}
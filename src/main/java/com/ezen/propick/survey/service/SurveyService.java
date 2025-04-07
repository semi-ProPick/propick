package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.survey.SurveyDTO;


public interface SurveyService {
    // 특정 설문 ID로 설문 데이터를 가져오는 메서드
    SurveyDTO getSurveyById(Integer surveyId);
}

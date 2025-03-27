package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyResponseIdDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import com.ezen.propick.survey.service.SurveyResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/survey-responses")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;

    @PostMapping
    /*
    * ResponseEntity<T>의 의미
    * ResponseEntity<String>	간단한 텍스트 메시지 응답
    * ResponseEntity<DTO>	객체(JSON) 응답 (예: SurveyDTO, ResultDTO)
    * ResponseEntity<Void>	응답 바디 없음 (상태 코드만 보냄)
    * */
    public ResponseEntity<SurveyResponseIdDTO> saveSurveyResponse(@RequestBody SurveyResponseRequestDTO requestDto) {
        Integer userId = 1; //  실제 로그인 사용자와 연동 예정
        Integer responseId = surveyResponseService.saveSurveyResponse(requestDto, userId);
        return ResponseEntity.ok(new SurveyResponseIdDTO(responseId));
    }
}
package com.ezen.propick.survey.controller;

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
    public ResponseEntity<String> saveSurveyResponse(@RequestBody SurveyResponseRequestDTO requestDto) {
        // TODO: 로그인 연동 시 사용자 ID를 SecurityContext 등에서 추출
        Integer userId = 1; // 테스트용

        surveyResponseService.saveSurveyResponse(requestDto, userId);
        return ResponseEntity.ok("설문 응답이 저장되었습니다.");
    }
}
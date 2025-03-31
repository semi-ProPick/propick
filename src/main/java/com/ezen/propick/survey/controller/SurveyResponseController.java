package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyResponseIdDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseUserDTO;
import com.ezen.propick.survey.service.SurveyResponseService;
import com.ezen.propick.survey.service.SurveyResponseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/survey-responses")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;
    private final SurveyResponseUserService surveyResponseUserService;

    /*
    * ResponseEntity<T>의 의미
    * ResponseEntity<String>	간단한 텍스트 메시지 응답
    * ResponseEntity<DTO>	객체(JSON) 응답 (예: SurveyDTO, ResultDTO)
    * ResponseEntity<Void>	응답 바디 없음 (상태 코드만 보냄)
    * */
    // [1] 설문 응답 저장 (공통)
    @PostMapping
    public ResponseEntity<SurveyResponseIdDTO> saveSurveyResponse(@RequestBody SurveyResponseRequestDTO requestDto) {
        Integer userId = 1; // 로그인된 사용자 ID와 연동 예정
        Integer responseId = surveyResponseService.saveSurveyResponse(requestDto, userId);
        return ResponseEntity.ok(new SurveyResponseIdDTO(responseId));
    }

    // [2] 마이페이지 - 회원 본인 설문 응답 리스트
    @GetMapping("/my_survey")
    public ResponseEntity<List<SurveyResponseUserDTO>> getMySurveyResponses() {
        Integer userId = 1;
        return ResponseEntity.ok(surveyResponseUserService.getResponsesByUser(userId));
    }

    // [3] 마이페이지 - 회원 본인 설문 응답 삭제
    @DeleteMapping("/my_survey/{responseId}")
    public ResponseEntity<Void> deleteMyResponse(@PathVariable Integer responseId) {
        Integer userId = 1;
        surveyResponseUserService.deleteByUser(responseId, userId);
        return ResponseEntity.noContent().build();
    }

}
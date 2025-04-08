package com.ezen.propick.survey.controller;

import com.ezen.propick.auth.model.AuthDetails;
import com.ezen.propick.survey.dto.survey.SurveyResponseIdDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseUserDTO;
import com.ezen.propick.survey.service.RecommendationService;
import com.ezen.propick.survey.service.SurveyResponseService;
import com.ezen.propick.survey.service.SurveyResponseUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/survey-responses")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;
    private final SurveyResponseUserService surveyResponseUserService;
    private final RecommendationService recommendationService;


    /*
    * ResponseEntity<T>의 의미
    * ResponseEntity<String>	간단한 텍스트 메시지 응답
    * ResponseEntity<DTO>	객체(JSON) 응답 (예: SurveyDTO, ResultDTO)
    * ResponseEntity<Void>	응답 바디 없음 (상태 코드만 보냄)
    * */



        //[1] 설문 응답 저장
        @PostMapping
        public ResponseEntity<SurveyResponseIdDTO> saveSurveyResponse(
                @RequestBody SurveyResponseRequestDTO requestDto,
                @AuthenticationPrincipal AuthDetails userDetails
        ) {
            String userId = userDetails.getUserId();
            log.info(" 설문 저장 요청 userId={}, surveyId={}", userId, requestDto.getSurveyId());

            // 1. 설문 응답 저장
            Integer responseId = surveyResponseService.saveSurveyResponse(requestDto, userId);

            // 2. 응답 기반 추천 생성
            recommendationService.createAndSaveRecommendation(responseId, 1, userId);

            // 3. 응답 ID 반환
            return ResponseEntity.ok(new SurveyResponseIdDTO(responseId));
        }


        // [2] 마이페이지 - 설문 응답 목록
        @GetMapping("/my_survey")
        public ResponseEntity<List<SurveyResponseUserDTO>> getMySurveyResponses(
                @AuthenticationPrincipal AuthDetails userDetails
        ) {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 🔒 인증 실패 시 401 반환
            }

            String userId = userDetails.getUserId();
            return ResponseEntity.ok(surveyResponseUserService.getResponsesByUserId(userId));
        }

        // [3] 마이페이지 - 설문 응답 삭제
        @DeleteMapping("/my_survey/{responseId}")
        public ResponseEntity<Void> deleteMyResponse(
                @PathVariable Integer responseId,
                @AuthenticationPrincipal AuthDetails userDetails
        ) {
            String userId = userDetails.getUserId();
            surveyResponseUserService.deleteByUserId(responseId, userId);
            return ResponseEntity.noContent().build();
        }
    }
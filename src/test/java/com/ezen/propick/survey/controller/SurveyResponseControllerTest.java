package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.survey.SurveyResponseIdDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SurveyResponseControllerTest {

//SurveyResponseController에 작성
//    @PostMapping("/test")
//    public ResponseEntity<SurveyResponseIdDTO> testSaveSurveyResponse(
//            @RequestBody SurveyResponseRequestDTO requestDto,
//            @RequestParam String userId // 💡 쿼리 파라미터로 받음
//    ) {
//        Integer responseId = surveyResponseService.saveSurveyResponse(requestDto, userId);
//        return ResponseEntity.ok(new SurveyResponseIdDTO(responseId));
//    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 설문_응답_저장_테스트_간단버전() throws Exception {
        String requestBody = """
                {
                  "surveyId": 1,
                  "answers": [
                    { "questionId": 3, "selectedOptionIds": [101] },
                    { "questionId": 4, "selectedOptionIds": [102] },
                    { "questionId": 5, "selectedOptionIds": [103] },
                    { "questionId": 6, "selectedOptionIds": [1] },
                    { "questionId": 7, "selectedOptionIds": [5] }
                  ]
                }
                """;

        mockMvc.perform(post("/api/survey-responses/test")
                        .param("userId", "thgp1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseId").exists());
    }
}
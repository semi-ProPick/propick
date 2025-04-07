package com.ezen.propick.survey.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SurveyFullFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 설문_전체_플로우_테스트() throws Exception {
        // 1. 설문 응답 저장
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

        String responseContent = mockMvc.perform(post("/api/survey-responses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 2. 응답 ID 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseContent);
        Integer responseId = rootNode.get("responseId").asInt();

        // 3. 분석 API 호출 및 결과 검증
        mockMvc.perform(post("/api/survey-analysis/{responseId}", responseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmi").exists())
                .andExpect(jsonPath("$.recommendedTypes").isArray())
                .andExpect(jsonPath("$.avoidTypes").isArray())
                .andExpect(jsonPath("$.intakeTiming").exists())
                .andExpect(jsonPath("$.warningMessages").isArray());
    }
}
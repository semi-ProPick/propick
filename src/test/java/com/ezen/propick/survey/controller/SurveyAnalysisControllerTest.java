package com.ezen.propick.survey.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest
@AutoConfigureMockMvc
class SurveyAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 분석_API_정상작동_테스트() throws Exception {

        Integer testResponseId = 2;

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
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. responseId 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseContent);
        Integer responseId = rootNode.get("responseId").asInt();

        // 3. 분석 API 호출
        mockMvc.perform(post("/api/survey-analysis/{responseId}", responseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmi").exists())
                .andExpect(jsonPath("$.recommendedTypes").isArray())
                .andExpect(jsonPath("$.warningMessages").isArray());

    }
}
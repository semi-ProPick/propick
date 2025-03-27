package com.ezen.propick.survey;

import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.ezen.propick.user.enumpackage.Gender;
import com.ezen.propick.user.enumpackage.Role;

import java.time.LocalDate;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SurveyResponseControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        User user = User.builder()
                .userNo(1)
                .userId("testuser")
                .userPwd("1234")
                .userName("테스트유저")
                .userPhone("01012345678")
                .userGender(Gender.Female)
                .userBirth(new Date())  //
                .userRole(Role.User)
                .build();

        userRepository.save(user);
    }
    @Test
    public void 설문_응답_저장_API_테스트() throws Exception {
        String requestBody = """
            {
              "surveyId": 1,
              "answers": [
                {
                  "questionId": 6,
                  "selectedOptionIds": [1, 2]
                },
                {
                  "questionId": 7,
                  "selectedOptionIds": [5]
                }
              ]
            }
        """;

        mockMvc.perform(
                        post("/api/survey-responses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("설문 응답이 저장되었습니다."));
    }
}
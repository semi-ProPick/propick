package com.ezen.propick.survey.service;
import com.ezen.propick.survey.dto.survey.AnswerDTO;
import com.ezen.propick.survey.dto.survey.SurveyResponseRequestDTO;
import com.ezen.propick.survey.entity.*;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.survey.repository.*;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyResponseOptionRepository surveyResponseOptionRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SurveyQuestionsRepository questionRepository;
    private final SurveyOptionsRepository optionRepository;


    public Integer saveSurveyResponse(SurveyResponseRequestDTO requestDto, Integer userNo) {
        // 1. Survey, User Entity 조회
        Survey survey = surveyRepository.findById(requestDto.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("설문을 찾을 수 없습니다."));

        User user = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2.  SurveyResponse 생성
        SurveyResponse response = SurveyResponse.builder()
                .surveyId(survey)
                .userNo(user)
                .responseStatus(ResponseStatus.ACTIVE)
                .responseDate(LocalDateTime.now())
                .build();

        surveyResponseRepository.save(response);


        // 3. 각 질문 응답에 대한 선택지 저장(다중 선택 고려)
        for (AnswerDTO answer : requestDto.getAnswers()) {
            SurveyQuestions question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

            for (Integer optionId : answer.getSelectedOptionIds()) {
                SurveyOptions option = optionRepository.findById(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("선택지를 찾을 수 없습니다."));

                SurveyResponseOption responseOption = SurveyResponseOption.builder()
                        .response(response)
                        .option(option)
                        .build();

                surveyResponseOptionRepository.save(responseOption);
            }
        }
        // 이후 응답 ID 반환
        return response.getResponseId();
    }
}

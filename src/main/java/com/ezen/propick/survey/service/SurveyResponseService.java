package com.ezen.propick.survey.service;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
// 설문 저장/분석 관련만 유지
public class SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyResponseOptionRepository surveyResponseOptionRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SurveyQuestionsRepository questionRepository;
    private final SurveyOptionsRepository optionRepository;
    private final RecommendationService recommendationService;

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

        //4. 추천 결과 생성 및 저장 로직 호출
        SurveyResultInputDTO inputDTO = createInputDTO(requestDto, user); // 아래에서 추가로 작성
        Integer recommendedProductId = decideRecommendedProductId(inputDTO); // 제품 추천 로직(임시로 설정하거나 구현 필요)

        recommendationService.createAndSaveRecommendation(
                response.getResponseId(), inputDTO, recommendedProductId, userNo
        );

        // 이후 응답 ID 반환
        return response.getResponseId();
    }

        // ✅ 추가로 필요한 메서드 (설문 응답 → 입력 DTO 변환)
        private SurveyResultInputDTO createInputDTO(SurveyResponseRequestDTO requestDto, User user) {
            double heightCm = Double.parseDouble(findOptionTextByQuestionId(requestDto, 4));
            double weightKg = Double.parseDouble(findOptionTextByQuestionId(requestDto, 5));
            int age = Integer.parseInt(findOptionTextByQuestionId(requestDto, 3));

            return SurveyResultInputDTO.builder()
                    .heightCm(heightCm)
                    .weightKg(weightKg)
                    .age(age)
                    .healthConcerns(toConcernMap(extractHealthConcerns(requestDto)))
                    .purpose(extractPurpose(requestDto))
                    .workoutFreq(extractWorkoutFrequency(requestDto))
                    .build();
        }
    private Map<String, Integer> toConcernMap(List<String> list) {
        return list.stream()
                .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
    }

    // 특정 질문ID로 선택된 옵션 텍스트 찾기 메서드
    private String findOptionTextByQuestionId(SurveyResponseRequestDTO requestDto, int questionId) {
        return requestDto.getAnswers().stream()
                .filter(answer -> answer.getQuestionId() == questionId)
                .findFirst()
                .flatMap(answer -> getSelectedOptionTexts(answer).stream().findFirst())
                .orElseThrow(() -> new IllegalArgumentException("질문 ID " + questionId + "의 응답을 찾을 수 없습니다."));
    }


    // 하위 질문을 고려한 건강 고민 추출 메서드
    private List<String> extractHealthConcerns(SurveyResponseRequestDTO requestDto) {

        List<String> predefinedHealthConcerns = List.of(
                "신장 질환", "간 질환", "심혈관 질환",
                "유당불내증", "여드름", "피로",
                "부종", "관절염", "수면장애", "변비", "소화불량"
        );

        Map<String, Integer> concernToSubQuestionId = Map.of(
                "소화, 장", 9,
                "피부 질환", 10,
                "신장 부담", 11,
                "수면 장애", 12,
                "관절 건강", 13,
                "간 건강", 14,
                "혈관 건강", 15
        );

        // 상위 질문에서 선택된 항목 (질문ID: 8)
        List<String> mainConcerns = requestDto.getAnswers().stream()
                .filter(answer -> answer.getQuestionId() == 8)
                .flatMap(answer -> getSelectedOptionTexts(answer).stream())
                .collect(Collectors.toList());

        List<String> finalConcerns = mainConcerns.stream().flatMap(mainConcern -> {
                    Integer subQuestionId = concernToSubQuestionId.get(mainConcern);
                    if (subQuestionId != null) {
                        // 하위 질문이 있으면 하위 질문 응답을 사용
                        return requestDto.getAnswers().stream()
                                .filter(answer -> answer.getQuestionId() == subQuestionId)
                                .flatMap(answer -> getSelectedOptionTexts(answer).stream());
                    } else {
                        // 하위 질문이 없으면 상위 질문 응답을 그대로 사용
                        return Stream.of(mainConcern);
                    }
                })
                .filter(predefinedHealthConcerns::contains)
                .distinct()
                .collect(Collectors.toList());

        return finalConcerns;
    }


    // 옵션 ID 리스트를 이용해 옵션 텍스트 리스트 반환
    // 옵션 ID로 옵션 텍스트를 조회하는 메서드 (AnswerDTO에서 호출용)
    private List<String> getSelectedOptionTexts(AnswerDTO answer) {
        return answer.getSelectedOptionIds().stream()
                .map(optionId -> optionRepository.findById(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다.")))
                .map(SurveyOptions::getOptionText)
                .collect(Collectors.toList());
    }
    // 목적 추출 메서드
    private String extractPurpose(SurveyResponseRequestDTO requestDto) {
        List<String> purposeOptions = List.of("근육 증가", "다이어트", "단백질 보충", "식사 대용");

        return requestDto.getAnswers().stream()
                .flatMap(answer -> getSelectedOptionTexts(answer).stream())
                .filter(purposeOptions::contains)
                .findFirst()
                .orElse("단백질 보충"); // 기본값
    }


    private String extractWorkoutFrequency(SurveyResponseRequestDTO requestDto) {
        return requestDto.getAnswers().stream()
                .flatMap(answer -> getSelectedOptionTexts(answer).stream())
                .filter(option -> option.matches("주[0-9~]+회"))
                .findFirst()
                .orElse("주1회"); // 기본값
    }



    private Integer decideRecommendedProductId(SurveyResultInputDTO inputDTO) {
        // 초기 임시 제품 ID 설정 (실제 로직은 추후 추가)
        return 1; // 현재는 간단히 임시로 제품 id를 반환하도록 하였으며, 이후 알고리즘을 추가하면 됩니다.
    }



}

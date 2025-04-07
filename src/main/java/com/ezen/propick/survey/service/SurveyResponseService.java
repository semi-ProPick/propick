package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.dto.survey.*;
import com.ezen.propick.survey.entity.*;
import com.ezen.propick.survey.enumpackage.QuestionType;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.survey.repository.*;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyResponseOptionRepository surveyResponseOptionRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SurveyQuestionsRepository questionRepository;
    private final SurveyOptionsRepository optionRepository;

    /**
     * 설문 응답 저장
     */
    public Integer saveSurveyResponse(SurveyResponseRequestDTO dto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Survey survey = surveyRepository.findById(dto.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("설문지를 찾을 수 없습니다."));


        SurveyResponse response = SurveyResponse.builder()
                .user(user)
                .surveyId(survey)
                .responseDate(LocalDateTime.now())
                .responseStatus(ResponseStatus.ACTIVE)
                .build();
        surveyResponseRepository.save(response);

        for (AnswerDTO answer : dto.getAnswers()) {
            SurveyQuestions question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

            // 자유 텍스트 응답 유효성 검증
            if (answer.getFreeTextAnswer() != null && !answer.getFreeTextAnswer().isBlank()) {
                if (question.getQuestionCode().equals("AGE")) {
                    int age = parseInt(answer.getFreeTextAnswer(), -1);
                    if (age < 10 || age > 110) {
                        throw new IllegalArgumentException("나이는 10~110 사이로 입력해주세요.");
                    }
                } else if (question.getQuestionCode().equals("HEIGHT")) {
                    int height = parseInt(answer.getFreeTextAnswer(), -1);
                    if (height < 100 || height > 250) {
                        throw new IllegalArgumentException("키는 100~250cm 사이로 입력해주세요.");
                    }
                } else if (question.getQuestionCode().equals("WEIGHT")) {
                    int weight = parseInt(answer.getFreeTextAnswer(), -1);
                    if (weight < 30 || weight > 190) {
                        throw new IllegalArgumentException("몸무게는 30~190kg 사이로 입력해주세요.");
                    }
                }

                // 저장
                SurveyResponseOption responseOption = SurveyResponseOption.builder()
                        .response(response)
                        .option(null)
                        .freeTextAnswer(answer.getFreeTextAnswer())
                        .question(question)
                        .build();
                surveyResponseOptionRepository.save(responseOption);
            }
            // 선택지 응답 저장
            if (answer.getSelectedOptionIds() != null && !answer.getSelectedOptionIds().isEmpty()) {
                for (Integer optionId : answer.getSelectedOptionIds()) {
                    SurveyOptions option = optionRepository.findById(optionId)
                            .orElseThrow(() -> new IllegalArgumentException("선택지를 찾을 수 없습니다."));

                    SurveyResponseOption responseOption = SurveyResponseOption.builder()
                            .response(response)
                            .option(option)
                            .question(question)
                            .build();
                    surveyResponseOptionRepository.save(responseOption);
                }
            }
        }

        return response.getResponseId();
    }
    /**
     * 설문 응답 ID 기반으로 분석용 DTO 생성
     */
    public SurveyResultInputDTO getSurveyResultInputDTO(Integer responseId) {
        SurveyResponse response = surveyResponseRepository.findByIdWithOptions(responseId) // ✅ fetch join 적용
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));

        Survey survey = response.getSurveyId();
        SurveyDTO surveyDTO = convertToSurveyDTO(survey);

        return buildInputDTOFromSurvey(response, surveyDTO);
    }

    /**
     * 응답 데이터와 설문 구조를 기반으로 분석용 DTO 생성
     */
    private SurveyResultInputDTO buildInputDTOFromSurvey(SurveyResponse response, SurveyDTO surveyDTO) {
        List<SurveyResponseOption> options = Optional.ofNullable(response.getSurveyResponseOptions())
                .orElse(new ArrayList<>());

        Set<Integer> selectedOptionIds = options.stream()
                .filter(opt -> opt.getOption() != null)
                .map(opt -> opt.getOption().getOptionId())
                .collect(Collectors.toSet());

        Map<Integer, String> freeTextAnswerMap = options.stream()
                .filter(opt -> opt.getOption() == null && opt.getFreeTextAnswer() != null)
                .collect(Collectors.toMap(opt -> opt.getQuestion().getQuestionId(), SurveyResponseOption::getFreeTextAnswer));

        Map<String, String> singleValueMap = new HashMap<>();
        Map<String, List<String>> multiValueMap = new HashMap<>();

        for (SurveyQuestionDTO question : surveyDTO.getQuestions()) {
            String questionCode = question.getQuestionCode();
            QuestionType type = question.getQuestionType();

            if (type == QuestionType.Text) {
                String freeTextAnswer = freeTextAnswerMap.get(question.getQuestionId());
                if (freeTextAnswer != null && !freeTextAnswer.isBlank()) {
                    singleValueMap.put(questionCode, freeTextAnswer);
                }
            } else {
                for (SurveyOptionsDTO option : flattenOptions(question.getOptions())) {
                    if (selectedOptionIds.contains(option.getOptionId())) {
                        String code = option.getOptionCode();
                        if (code == null || code.isBlank()) {
                            log.warn("❗ optionId {} 의 optionCode 가 null 또는 비어있습니다.", option.getOptionId());
                            continue;
                        }
                        log.info("질문코드: {}, 선택된 옵션 ID: {}, 코드: {}", questionCode, option.getOptionId(), code);

                        if (type == QuestionType.Multiple) {
                            multiValueMap.computeIfAbsent(questionCode, k -> new ArrayList<>()).add(code);
                        } else if (type == QuestionType.Single) {
                            singleValueMap.putIfAbsent(questionCode, code);
                        }
                    }
                }
            }
        }

        log.info("✅ 최종 SINGLE MAP: {}", singleValueMap);
        log.info("✅ 최종 MULTI MAP: {}", multiValueMap);

        List<String> healthConcerns = multiValueMap.getOrDefault("HEALTH_CONCERN", new ArrayList<>());

        return SurveyResultInputDTO.builder()
                .name(singleValueMap.get("NAME"))
                .gender(singleValueMap.get("GENDER"))
                .age(parseInt(singleValueMap.get("AGE"), 30))
                .heightCm(parseDouble(singleValueMap.get("HEIGHT"), 170))
                .weightKg(parseDouble(singleValueMap.get("WEIGHT"), 60))
                .purpose(singleValueMap.getOrDefault("PURPOSE", "PROTEIN_SUPPORT"))
                .workoutFreq(singleValueMap.getOrDefault("WORKOUT", "LOW"))
                .healthConcerns(toConcernMap(healthConcerns))
                .build();
    }

    /**
     * Survey → SurveyDTO 변환 (option 트리 포함)
     */
    private SurveyDTO convertToSurveyDTO(Survey survey) {
        return SurveyDTO.builder()
                .surveyId(survey.getSurveyId())
                .surveyTitle(survey.getSurveyTitle())
                .surveyCreatedAt(survey.getSurveyCreatedAt())
                .surveyUpdatedAt(survey.getSurveyUpdatedAt())
                .surveyStatus(survey.getSurveyStatus().name())
                .questions(
                        survey.getQuestions().stream()
                                .map(q -> SurveyQuestionDTO.builder()
                                        .questionId(q.getQuestionId())
                                        .questionText(q.getQuestionText())
                                        .questionCode(q.getQuestionCode())
                                        .questionType(q.getQuestionType())
                                        .isOptional(q.getIsOptional())
                                        .options(
                                                q.getOptions().stream()
                                                        .map(this::convertToOptionDTO)
                                                        .collect(Collectors.toList())
                                        )
                                        .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }

    private SurveyOptionsDTO convertToOptionDTO(SurveyOptions option) {
        return SurveyOptionsDTO.builder()
                .optionId(option.getOptionId())
                .optionText(option.getOptionText())
                .optionCode(option.getOptionCode())
                .questionId(option.getQuestionId().getQuestionId())
                .parentOptionId(option.getParentOption() != null ? option.getParentOption().getOptionId() : null)
                .childOptions(
                        option.getChildOptions().stream()
                                .map(this::convertToOptionDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private Map<String, Integer> toConcernMap(List<String> list) {
        return list.stream()
                .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
    }

    private List<SurveyOptionsDTO> flattenOptions(List<SurveyOptionsDTO> options) {
        List<SurveyOptionsDTO> result = new ArrayList<>();
        for (SurveyOptionsDTO option : options) {
            result.add(option);
            result.addAll(flattenOptions(option.getChildOptions()));
        }
        return result;
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

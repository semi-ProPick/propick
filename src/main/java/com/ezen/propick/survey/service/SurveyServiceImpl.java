package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.survey.SurveyDTO;
import com.ezen.propick.survey.dto.survey.SurveyOptionsDTO;
import com.ezen.propick.survey.dto.survey.SurveyQuestionDTO;
import com.ezen.propick.survey.entity.Survey;
import com.ezen.propick.survey.entity.SurveyOptions;
import com.ezen.propick.survey.entity.SurveyQuestions;
import com.ezen.propick.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // 서비스 빈으로 등록
@RequiredArgsConstructor // 생성자 자동 생성 (final 필드 주입)
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    //주어진 surveyId로 설문 데이터를 조회하고 DTO로 변환
    @Override
    public SurveyDTO getSurveyById(Integer surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("설문을 찾을 수 없습니다."));

        // SurveyQuestions -> SurveyQuestionDTO 변환
        List<SurveyQuestionDTO> questionDTOs = survey.getQuestions().stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());

        return SurveyDTO.builder()
                .surveyId(survey.getSurveyId())
                .surveyTitle(survey.getSurveyTitle())
                .surveyCreatedAt(survey.getSurveyCreatedAt())
                .surveyUpdatedAt(survey.getSurveyUpdatedAt())
                .surveyStatus(survey.getSurveyStatus().name())
                .questions(questionDTOs)
                .build();
    }

    /**
     * SurveyQuestions 엔티티를 SurveyQuestionDTO로 변환
     */
    private SurveyQuestionDTO convertToQuestionDTO(SurveyQuestions question) {
        List<SurveyOptionsDTO> optionDTOs = question.getOptions().stream()
                .map(this::convertToOptionDTO)
                .collect(Collectors.toList());

        return SurveyQuestionDTO.builder()
                .questionId(question.getQuestionId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType().name())
                .isOptional(question.getIsOptional())
                .options(optionDTOs)
                .build();
    }

    /**
     * SurveyOptions 엔티티를 SurveyOptionsDTO로 변환
     */
    private SurveyOptionsDTO convertToOptionDTO(SurveyOptions option) {
        List<SurveyOptionsDTO> childDTOs = option.getChildOptions().stream()
                .map(this::convertToOptionDTO) // 재귀적으로 자식도 처리
                .collect(Collectors.toList());

        return SurveyOptionsDTO.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuestionId().getQuestionId())
                .optionText(option.getOptionText())
                .parentOptionId(option.getParentOption() != null ? option.getParentOption().getOptionId() : null)
                .childOptions(childDTOs)
                .build();
    }
}

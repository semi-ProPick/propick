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
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    @Override
    public SurveyDTO getSurveyById(Integer surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("설문을 찾을 수 없습니다."));

        // 🔥 하위 증상 질문(questionId: 9~15)을 제외하고 상위 질문만 처리
        List<Integer> excludedDetailQuestionIds = List.of(9, 10, 11, 12, 13, 14, 15);

        List<SurveyQuestionDTO> questionDTOs = survey.getQuestions().stream()
                .filter(q -> !excludedDetailQuestionIds.contains(q.getQuestionId())) // 제외
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

    private SurveyOptionsDTO convertToOptionDTO(SurveyOptions option) {
        List<SurveyOptionsDTO> childDTOs = option.getChildOptions().stream()
                .map(this::convertToOptionDTO) // 재귀적으로 처리
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

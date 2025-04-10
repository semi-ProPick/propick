package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.survey.SurveyDTO;
import com.ezen.propick.survey.dto.survey.SurveyOptionsDTO;
import com.ezen.propick.survey.dto.survey.SurveyQuestionDTO;
import com.ezen.propick.survey.entity.Survey;
import com.ezen.propick.survey.entity.SurveyOptions;
import com.ezen.propick.survey.entity.SurveyQuestions;
import com.ezen.propick.survey.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
//설문 서비스 구현체
public class SurveyServiceImpl implements SurveyService {

    // 설문 데이터를 가져올 수 있도록 SurveyRepository 의존성 주입
    private final SurveyRepository surveyRepository;


    @Override
    public SurveyDTO getSurveyById(Integer surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("설문을 찾을 수 없습니다."));

        //상위 질문만 필터링
        // 건강고민 하위 질문(9-15)제외 후 설문 상위 질문 DTO 변환
        List<Integer> excludedDetailQuestionIds = List.of(9, 10, 11, 12, 13, 14, 15);

        List<SurveyQuestionDTO> questionDTOs = survey.getQuestions().stream()
                .filter(q -> !excludedDetailQuestionIds.contains(q.getQuestionId()))
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());

        // SurveyDTO 빌더 패턴을 사용하여 설문 데이터를 반환
        return SurveyDTO.builder()
                .surveyId(survey.getSurveyId())
                .surveyTitle(survey.getSurveyTitle())
                .surveyCreatedAt(survey.getSurveyCreatedAt())
                .surveyUpdatedAt(survey.getSurveyUpdatedAt())
                .surveyStatus(survey.getSurveyStatus().name())
                .questions(questionDTOs)
                .build();
    }


    // 선택지(childOptions)를 재귀적으로 순회하여 트리 구조의 선택지 생성
    // 프론트에서는 이 구조를 기반으로 상위 선택지 → 하위 선택지 렌더링 구성
    private SurveyQuestionDTO convertToQuestionDTO(SurveyQuestions question) {
        // 질문에 속한 선택지를 DTO로 변환
        List<SurveyOptionsDTO> optionDTOs = question.getOptions().stream()
                .map(this::convertToOptionDTO)
                .collect(Collectors.toList());

        // SurveyQuestionDTO 변환된 질문 객체 반환
        return SurveyQuestionDTO.builder()
                .questionId(question.getQuestionId())
                .questionText(question.getQuestionText())
                .questionCode(question.getQuestionCode())
                .questionType(question.getQuestionType())
                .isOptional(question.getIsOptional())
                .options(optionDTOs)
                .build();
    }

    //SurveyOptions 엔티티를 SurveyOptionsDTO로 변환하는 메서드 (재귀적으로 처리)
    private SurveyOptionsDTO convertToOptionDTO(SurveyOptions option) {

        List<SurveyOptionsDTO> childDTOs = option.getChildOptions().stream()
                .map(this::convertToOptionDTO)
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
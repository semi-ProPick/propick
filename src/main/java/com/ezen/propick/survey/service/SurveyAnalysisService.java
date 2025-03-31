package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import com.ezen.propick.survey.engine.ProteinRecommendationEngine;
import com.ezen.propick.survey.entity.SurveyQuestions;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.entity.SurveyResponseOption;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyAnalysisService {

    private final ProteinRecommendationEngine recommendationEngine;
    private final SurveyResponseRepository responseRepository;

    public SurveyRecommendationResultDTO analyzeSurvey(Integer responseId) {
        SurveyResponse response = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));

        SurveyResultInputDTO inputDTO = convertResponseToAnalysisDTO(response);

        return recommendationEngine.generate(inputDTO);
    }

    private SurveyResultInputDTO convertResponseToAnalysisDTO(SurveyResponse response) {
        Map<Integer, String> answerMap = new HashMap<>();
        Map<Integer, List<String>> answerListMap = new HashMap<>();

        for (SurveyResponseOption option : response.getSurveyResponseOptions()) {
            SurveyQuestions question = option.getOption().getQuestionId();
            Integer questionId = question.getQuestionId();
            String optionText = option.getOption().getOptionText();

            if (Arrays.asList(1, 2, 3, 4, 5, 6, 7).contains(questionId)) {
                answerMap.put(questionId, optionText);
            } else if (questionId >= 8) {
                answerListMap.computeIfAbsent(questionId, k -> new ArrayList<>()).add(optionText);
            }
        }

        List<String> finalHealthConcerns = new ArrayList<>();
        List<String> mainConcerns = answerListMap.getOrDefault(8, List.of());

        Map<String, Integer> concernToSubQuestionId = Map.of(
                "소화, 장", 9,
                "피부 질환", 10,
                "신장 부담", 11,
                "수면 장애", 12,
                "관절 건강", 13,
                "간 건강", 14,
                "혈관 건강", 15
        );

        for (String concern : mainConcerns) {
            Integer subQuestionId = concernToSubQuestionId.get(concern);
            if (subQuestionId != null && answerListMap.containsKey(subQuestionId)) {
                finalHealthConcerns.addAll(answerListMap.get(subQuestionId));
            } else {
                finalHealthConcerns.add(concern);
            }
        }

        return SurveyResultInputDTO.builder()
                .name(answerMap.get(1))
                .gender(answerMap.get(2))
                .age(Integer.parseInt(answerMap.get(3)))
                .heightCm(Double.parseDouble(answerMap.get(4)))
                .weightKg(Double.parseDouble(answerMap.get(5)))
                .purpose(answerMap.get(6))
                .workoutFreq(answerMap.get(7))
                .healthConcerns(toConcernMap(finalHealthConcerns))
                .build();
    }
    private Map<String, Integer> toConcernMap(List<String> list) {
        return list.stream()
                .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
    }
}

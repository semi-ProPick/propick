package com.ezen.propick.survey.service;
import com.ezen.propick.survey.dto.recommendation.SatisfactionDTO;
import com.ezen.propick.survey.entity.Satisfaction;
import com.ezen.propick.survey.entity.Survey;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.repository.SatisfactionRepository;
import com.ezen.propick.survey.repository.SurveyRepository;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class    SatisfactionServiceImpl implements SatisfactionService {

    private final SatisfactionRepository satisfactionRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyRepository surveyRepository;

    @Override
    public void save(SatisfactionDTO dto) {
        SurveyResponse response = surveyResponseRepository.findById(dto.getResponseId())
                .orElseThrow(() -> new IllegalArgumentException("응답 ID 없음"));

        if (satisfactionRepository.existsByResponse(response)) {
            return; // 이미 저장된 경우
        }

        Survey survey = surveyRepository.findById(dto.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("설문 ID 없음"));

        Satisfaction satisfaction = Satisfaction.builder()
                .surveyId(survey)
                .response(response)
                .satisfactionScore(dto.getSatisfactionScore())
                .build();

        satisfactionRepository.save(satisfaction);
    }
    @Override
    public List<SatisfactionDTO> getAllSatisfaction() {
        List<Satisfaction> list = satisfactionRepository.findAll();
        return list.stream().map(s -> SatisfactionDTO.builder()
                        .satisfactionId(s.getSatisfactionId())
                        .surveyId(s.getSurveyId().getSurveyId())
                        .responseId(s.getResponse().getResponseId())
                        .satisfactionScore(s.getSatisfactionScore())
                        .satisfactionCreatedAt(s.getSatisfactionCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}

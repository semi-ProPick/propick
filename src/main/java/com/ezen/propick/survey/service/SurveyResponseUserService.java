package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.survey.SurveyResponseUserDTO;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyResponseUserService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final RecommendationRepository recommendationRepository;

    // 사용자 응답 목록 조회
    public List<SurveyResponseUserDTO> getResponsesByUser(Integer userId) {
        List<SurveyResponse> responses = surveyResponseRepository.findAllByUserNo_UserNoAndResponseStatus(userId, ResponseStatus.ACTIVE);
        return responses.stream()
                .map(SurveyResponseUserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 사용자 응답 삭제 (soft delete)
    @Transactional
    public void deleteByUser(Integer responseId, Integer userId) {
        SurveyResponse response = surveyResponseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("설문 응답을 찾을 수 없습니다."));

        if (!response.getUserNo().getUserNo().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        response.setResponseStatus(ResponseStatus.DELETED);
    }
}

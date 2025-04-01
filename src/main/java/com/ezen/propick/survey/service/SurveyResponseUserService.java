package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.survey.SurveyResponseUserDTO;
import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.survey.repository.RecommendationRepository;
import com.ezen.propick.survey.repository.SurveyResponseRepository;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyResponseUserService {

    private final UserRepository userRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    public List<SurveyResponseUserDTO> getResponsesByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<SurveyResponse> responses = surveyResponseRepository.findByUser(user);

        return responses.stream()
                .map(SurveyResponseUserDTO::fromEntity)
                .toList();
    }

    public void deleteByUserId(Integer responseId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        SurveyResponse response = surveyResponseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("응답 없음"));

        if (!response.getUser().equals(user)) {
            throw new SecurityException("본인의 응답만 삭제할 수 있습니다.");
        }

        surveyResponseRepository.delete(response);
    }
}

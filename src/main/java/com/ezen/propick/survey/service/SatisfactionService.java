package com.ezen.propick.survey.service;

import com.ezen.propick.survey.dto.recommendation.SatisfactionDTO;

import java.util.List;

public interface SatisfactionService {
        void save(SatisfactionDTO dto);
    List<SatisfactionDTO> getAllSatisfaction();

}


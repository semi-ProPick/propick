package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {
    List<SurveyResponse> findAllByUserNo_UserNoAndResponseStatus(Integer userId, ResponseStatus responseStatus);
}

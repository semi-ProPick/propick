package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponseRepository extends CrudRepository<SurveyResponse, Integer> {
}

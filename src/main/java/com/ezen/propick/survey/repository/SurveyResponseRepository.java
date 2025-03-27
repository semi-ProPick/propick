package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {

}

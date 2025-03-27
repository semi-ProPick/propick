package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/*설문지 조회*/
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {
}

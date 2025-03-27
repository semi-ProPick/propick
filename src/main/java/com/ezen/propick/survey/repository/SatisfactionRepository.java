package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Satisfaction;
import com.ezen.propick.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*만족도 저장, 조회*/
@Repository
public interface SatisfactionRepository extends JpaRepository<Satisfaction,Integer> {

}

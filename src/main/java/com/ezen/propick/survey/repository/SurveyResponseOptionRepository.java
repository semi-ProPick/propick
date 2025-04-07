package com.ezen.propick.survey.repository;


import com.ezen.propick.survey.entity.SurveyResponseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/*응답 옵션 저장*/
@Repository
public interface SurveyResponseOptionRepository extends JpaRepository<SurveyResponseOption, Integer> {
}

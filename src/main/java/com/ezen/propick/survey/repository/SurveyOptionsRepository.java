package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/*옵션 조회*/
@Repository
public interface SurveyOptionsRepository extends JpaRepository<SurveyOptions, Integer> {

}


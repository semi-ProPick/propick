package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*질문 조회*/
@Repository
public interface SurveyQuestionsRepository extends JpaRepository<SurveyQuestions, Integer> {

}
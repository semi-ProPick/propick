package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyQuestionsRepository extends JpaRepository<SurveyQuestions, Integer> {
}

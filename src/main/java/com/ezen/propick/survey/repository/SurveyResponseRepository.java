package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {
    List<SurveyResponse> findAllByUser_UserIdAndResponseStatus(String userId, ResponseStatus responseStatus);
    List<SurveyResponse> findByUser(User user);


}

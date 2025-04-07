package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.enumpackage.ResponseStatus;
import com.ezen.propick.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {
    List<SurveyResponse> findAllByUser_UserIdAndResponseStatus(String userId, ResponseStatus responseStatus);
    List<SurveyResponse> findByUser(User user);


    @Query("SELECT sr FROM SurveyResponse sr " +
            "JOIN FETCH sr.surveyResponseOptions sro " +
            "LEFT JOIN FETCH sro.option o " +
            "LEFT JOIN FETCH sro.question q " +
            "WHERE sr.responseId = :id")  // ✅ 올바른 필드명 사용
    Optional<SurveyResponse> findByIdWithOptions(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE SurveyResponse sr SET sr.responseStatus = com.ezen.propick.survey.enumpackage.ResponseStatus.DELETED WHERE sr.responseId = :responseId")
    void DeleteById(@Param("responseId") Integer responseId);

}

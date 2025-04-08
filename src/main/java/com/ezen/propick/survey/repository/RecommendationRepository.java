package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/*추천 저장, 조회*/
@Repository

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    // 추천 1개 조회 (설문 응답 ID + 로그인 ID 기준)
    Optional<Recommendation> findByResponseId_ResponseIdAndUser_UserId(Integer responseId, String userId);
    // 해당 유저의 모든 추천 목록 조회
    List<Recommendation> findAllByUser_UserId(String userId);

}

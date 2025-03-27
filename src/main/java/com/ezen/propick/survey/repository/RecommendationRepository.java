package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/*추천 저장, 조회*/
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {

}

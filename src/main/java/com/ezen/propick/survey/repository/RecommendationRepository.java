package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
}

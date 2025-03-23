package com.ezen.propick.survey.repository;

import com.ezen.propick.survey.entity.Satisfaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatisfactionRepository extends JpaRepository<Satisfaction,Integer> {
}

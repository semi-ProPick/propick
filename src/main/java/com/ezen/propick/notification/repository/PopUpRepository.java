package com.ezen.propick.notification.repository;

import com.ezen.propick.notification.Enum.PopUpStatus;
import com.ezen.propick.notification.entity.PopUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopUpRepository extends JpaRepository<PopUp, Integer> {
    @Query("SELECT p FROM PopUp p WHERE p.pStatus = :status")
    List<PopUp> findByPStatus(@Param("status") PopUpStatus pStatus);
}
package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.entity.UserPostBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaBoardRepository extends JpaRepository<QnaBoard, Integer> {
}

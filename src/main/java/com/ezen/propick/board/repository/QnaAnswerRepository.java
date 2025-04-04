package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.QnaAnswer;
import com.ezen.propick.board.entity.QnaBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Integer> {

    List<QnaAnswer> findAllByQnaBoard(QnaBoard qnaBoard);
    boolean existsByQnaBoard(QnaBoard qnaBoard);

}

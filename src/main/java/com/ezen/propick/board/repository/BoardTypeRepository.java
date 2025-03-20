package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.BoardType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {
    List<BoardType> findByBoardType(BoardType boardType, Pageable pageable);
}

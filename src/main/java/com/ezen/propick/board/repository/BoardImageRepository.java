package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
}

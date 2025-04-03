package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FnaBoardRepository extends JpaRepository<FnaBoard, Integer> {
    Page<FnaBoard> findByTitleContaining(String searchKeyword, Pageable pageable);
}

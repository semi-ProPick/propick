package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.UserPostBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostBoardRepository extends JpaRepository<UserPostBoard, Integer> {

    Page<UserPostBoard> findByTitleContaining(String searchKeyword, Pageable pageable);

}

package com.ezen.propick.board.repository;

import com.ezen.propick.board.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}

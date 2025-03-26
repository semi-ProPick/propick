package com.ezen.propick.bookmark.repository;

import com.ezen.propick.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
    List<Bookmark> findByUserNo(int userNo);
    boolean existsByUserNoAndProductId(int userNo, int productId);
}

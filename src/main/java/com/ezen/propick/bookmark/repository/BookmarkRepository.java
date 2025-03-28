package com.ezen.propick.bookmark.repository;

import com.ezen.propick.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
//    List<Bookmark> findByUserNo(int userNo);
    boolean existsByUserNoAndProductProductId(Integer userNo, Integer product);

    @Query("SELECT b FROM Bookmark b JOIN FETCH b.product WHERE b.userNo = :userNo")
    List<Bookmark> findByUserNo(@Param("userNo") Integer userNo);
}
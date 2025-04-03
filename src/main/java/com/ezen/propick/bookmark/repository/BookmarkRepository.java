package com.ezen.propick.bookmark.repository;

import com.ezen.propick.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserNoAndProduct_ProductId(String userNo, Integer productId); // Integer -> String

    List<Bookmark> findByUserNo(String userNo); // Integer -> String

    @Query("SELECT b.product.productId, COUNT(b) as bookmarkCount " +
            "FROM Bookmark b WHERE b.bookmarkStatus = 'ACTIVE' " +
            "GROUP BY b.product.productId " +
            "ORDER BY bookmarkCount DESC")
    List<Object[]> findTop3ByBookmarkCount();
}
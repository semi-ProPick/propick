package com.ezen.propick.bookmark.repository;

import com.ezen.propick.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

    @Query("SELECT b FROM Bookmark b WHERE b.user.userNo = :userNo AND b.bookmarkStatus = :status")
    List<Bookmark> findByUser_UserNoAndBookmarkStatus(Integer userNo, String status);

    Optional<Bookmark> findByUser_UserNoAndProduct_ProductId(Integer userNo, Integer productId);

    @Query("SELECT b.product.productId, COUNT(b) as bookmarkCount " +
            "FROM Bookmark b " +
            "WHERE b.bookmarkStatus = 'ACTIVE' " +
            "GROUP BY b.product.productId " +
            "ORDER BY bookmarkCount DESC")
    List<Object[]> findTopBookmarkedProducts(Pageable pageable);
}
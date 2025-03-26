package com.ezen.propick.bookmark.repository;

import com.ezen.propick.bookmark.entity.Bookmarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarksRepository extends JpaRepository<Bookmarks, Integer> {

    List<Bookmarks> findByCreatedBy(String createdBy);

    boolean existsByCreatedByAndUrl(String createdBy, String url); // 중복 체크용
}
package com.ezen.propick.bookmark.service;

import com.ezen.propick.bookmark.dto.BookmarksDTO;
import com.ezen.propick.bookmark.entity.Bookmarks;
import com.ezen.propick.bookmark.repository.BookmarksRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarksService {

    private static final Logger logger = LoggerFactory.getLogger(BookmarksService.class);

    private final BookmarksRepository bookmarksRepository;

    // 즐겨찾기 추가
    @Transactional
    public BookmarksDTO addBookmark(BookmarksDTO bookmarkDTO) {
        // 중복 체크
        if (bookmarksRepository.existsByCreatedByAndUrl(bookmarkDTO.getCreatedBy(), bookmarkDTO.getUrl())) {
            throw new IllegalStateException("이미 즐겨찾기한 URL입니다.");
        }

        // DTO -> 엔티티 변환
        Bookmarks bookmarks = Bookmarks.builder()
                .title(bookmarkDTO.getTitle())
                .url(bookmarkDTO.getUrl())
                .category(bookmarkDTO.getCategory())
                .createdBy(bookmarkDTO.getCreatedBy())
                .build();

        Bookmarks savedBookmarks = bookmarksRepository.save(bookmarks);
        return convertToDTO(savedBookmarks);
    }

    // createdBy로 즐겨찾기 목록 조회
    public List<BookmarksDTO> getBookmarksByCreatedBy(String createdBy) {
        try {
            List<Bookmarks> bookmarks = bookmarksRepository.findByCreatedBy(createdBy);
            return bookmarks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching bookmarks for createdBy {}: {}", createdBy, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // 즐겨찾기 삭제
    @Transactional
    public void removeBookmark(Integer bookmarkId) {
        if (!bookmarksRepository.existsById(bookmarkId)) {
            throw new IllegalArgumentException("해당 ID의 즐겨찾기가 존재하지 않습니다: " + bookmarkId);
        }
        bookmarksRepository.deleteById(bookmarkId);
    }

    // 특정 URL이 즐겨찾기되었는지 확인
    public boolean isBookmarked(String createdBy, String url) {
        return bookmarksRepository.existsByCreatedByAndUrl(createdBy, url);
    }

    // 엔티티 -> DTO 변환
    private BookmarksDTO convertToDTO(Bookmarks bookmarks) {
        return BookmarksDTO.builder()
                .id(bookmarks.getId())
                .title(bookmarks.getTitle())
                .url(bookmarks.getUrl())
                .category(bookmarks.getCategory())
                .createdBy(bookmarks.getCreatedBy())
                .createdAt(bookmarks.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
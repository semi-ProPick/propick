package com.ezen.propick.bookmark.service;

import com.ezen.propick.bookmark.Enum.BookmarkStatus;
import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.entity.Bookmark;
import com.ezen.propick.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkService.class);

    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public BookmarkDTO addBookmark(BookmarkDTO bookmarkDTO) {
        logger.info("Adding bookmark: {}", bookmarkDTO);

        if (bookmarkRepository.existsByUserNoAndProductId(bookmarkDTO.getUserNo(), bookmarkDTO.getProductId())) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setBookmark_status(BookmarkStatus.valueOf(bookmarkDTO.getStatus()));
        bookmark.setUserNo(bookmarkDTO.getUserNo());
        bookmark.setProductId(bookmarkDTO.getProductId());

        logger.info("Saving bookmark to DB: {}", bookmark);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        logger.info("Saved bookmark: {}", savedBookmark);
        return convertToDTO(savedBookmark);
    }

    public List<BookmarkDTO> getBookmarksByUserNo(Integer userNo) {
        try {
            List<Bookmark> bookmarks = bookmarkRepository.findByUserNo(userNo);
            return bookmarks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching bookmarks for userNo {}: {}", userNo, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Transactional
    public void removeBookmark(Integer bookmarkId) {
        if (!bookmarkRepository.existsById(bookmarkId)) {
            throw new IllegalArgumentException("해당 ID의 즐겨찾기가 존재하지 않습니다: " + bookmarkId);
        }
        bookmarkRepository.deleteById(bookmarkId);
    }

    public boolean isBookmarked(Integer userNo, Integer productId) {
        return bookmarkRepository.existsByUserNoAndProductId(userNo, productId);
    }

    private BookmarkDTO convertToDTO(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .id(bookmark.getBookmark_id())
                .status(bookmark.getBookmark_status().name())
                .userNo(bookmark.getUserNo())
                .productId(bookmark.getProductId())
                .build();
    }
}
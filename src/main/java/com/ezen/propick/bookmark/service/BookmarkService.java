package com.ezen.propick.bookmark.service;

import com.ezen.propick.bookmark.Enum.BookmarkStatus;
import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.entity.Bookmark;
import com.ezen.propick.bookmark.repository.BookmarkRepository;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Transactional
    public BookmarkDTO addBookmark(BookmarkDTO bookmarkDTO) {
        logger.info("Adding bookmark for userNo: {}, productId: {}", bookmarkDTO.getUserNo(), bookmarkDTO.getProductId());

        // 중복 체크
        if (bookmarkRepository.existsByUserNoAndProductProductId(bookmarkDTO.getUserNo(), bookmarkDTO.getProductId())) {
            logger.warn("Bookmark already exists for userNo: {}, productId: {}", bookmarkDTO.getUserNo(), bookmarkDTO.getProductId());
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        // Product 조회
        Product product = productRepository.findById(bookmarkDTO.getProductId())
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", bookmarkDTO.getProductId());
                    return new IllegalArgumentException("상품을 찾을 수 없습니다: " + bookmarkDTO.getProductId());
                });

        // Bookmark 생성
        Bookmark bookmark = Bookmark.builder()
                .bookmark_status(BookmarkStatus.valueOf(bookmarkDTO.getStatus()))
                .userNo(bookmarkDTO.getUserNo())
                .product(product)
                .build();

        // 저장
        logger.info("Saving bookmark to DB: {}", bookmark);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        logger.info("Saved bookmark: {}", savedBookmark);

        // DTO로 변환
        return convertToDTO(savedBookmark);
    }

    public List<BookmarkDTO> getBookmarksByUserNo(Integer userNo) {
        try {
            logger.info("Fetching bookmarks for userNo: {}", userNo);
            List<Bookmark> bookmarks = bookmarkRepository.findByUserNo(userNo);
            if (bookmarks.isEmpty()) {
                logger.warn("No bookmarks found for userNo: {}", userNo);
            }
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
        logger.info("Removing bookmark with ID: {}", bookmarkId);
        if (!bookmarkRepository.existsById(bookmarkId)) {
            logger.error("Bookmark not found with ID: {}", bookmarkId);
            throw new IllegalArgumentException("해당 ID의 즐겨찾기가 존재하지 않습니다: " + bookmarkId);
        }
        bookmarkRepository.deleteById(bookmarkId);
        logger.info("Bookmark removed successfully: {}", bookmarkId);
    }

    public boolean isBookmarked(Integer userNo, Integer productId) {
        boolean exists = bookmarkRepository.existsByUserNoAndProductProductId(userNo, productId);
        logger.info("Checked if productId: {} is bookmarked for userNo: {}, result: {}", productId, userNo, exists);
        return exists;
    }

    private BookmarkDTO convertToDTO(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .id(bookmark.getBookmark_id())
                .status(bookmark.getBookmark_status().name())
                .userNo(bookmark.getUserNo())
                .productId(bookmark.getProduct().getProductId())
                .productName(bookmark.getProduct().getProductName())
                .productPrice(bookmark.getProduct().getProductPrice())
                .build();
    }
}
package com.ezen.propick.bookmark.controller;

import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.service.BookmarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bookmark")
public class BookmarkController {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkController.class); // logger 추가

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/user/{userNo}")
    @ResponseBody
    public ResponseEntity<List<BookmarkDTO>> getBookmarksByUserNo(@PathVariable Integer userNo) {
        logger.info("Fetching bookmarks for userNo: {}", userNo);
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUserNo(userNo);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<BookmarkDTO> createBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        logger.info("Creating bookmark for userNo: {}, productId: {}", bookmarkDTO.getUserNo(), bookmarkDTO.getProductId());
        BookmarkDTO createdBookmark = bookmarkService.addBookmark(bookmarkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBookmark);
    }

    @DeleteMapping("/{bookmarkId}")
    @ResponseBody
    public ResponseEntity<Void> removeBookmark(@PathVariable Integer bookmarkId) {
        logger.info("Removing bookmark with id: {}", bookmarkId);
        bookmarkService.removeBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Boolean> isBookmarked(
            @RequestParam Integer userNo,
            @RequestParam Integer productId) {
        logger.info("Checking if productId: {} is bookmarked for userNo: {}", productId, userNo);
        boolean isBookmarked = bookmarkService.isBookmarked(userNo, productId);
        return ResponseEntity.ok(isBookmarked);
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<BookmarkDTO> toggleBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        logger.info("Toggling bookmark for userNo: {}, productId: {}", bookmarkDTO.getUserNo(), bookmarkDTO.getProductId());
        Integer userNo = bookmarkDTO.getUserNo();
        Integer productId = bookmarkDTO.getProductId();

        boolean isBookmarked = bookmarkService.isBookmarked(userNo, productId);
        if (isBookmarked) {
            BookmarkDTO existingBookmark = bookmarkService.getBookmarksByUserNo(userNo)
                    .stream()
                    .filter(b -> b.getProductId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Bookmark not found"));
            bookmarkService.removeBookmark(existingBookmark.getId());
            return ResponseEntity.ok().body(null);
        } else {
            BookmarkDTO createdBookmark = bookmarkService.addBookmark(bookmarkDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBookmark);
        }
    }

    @GetMapping("/favorite")
    public String showFavoritePage(@RequestParam Integer userNo, Model model) {
        logger.info("Rendering favorite page for userNo: {}", userNo);
        List<BookmarkDTO> bookmark = bookmarkService.getBookmarksByUserNo(userNo);
        if (bookmark == null) {
            logger.warn("No bookmarks found for userNo: {}", userNo);
            bookmark = new ArrayList<>();
        }
        model.addAttribute("bookmark", bookmark);
        return "main/favorite"; // "main/favorite"로 변경
    }
}
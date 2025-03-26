package com.ezen.propick.bookmark.controller;

import com.ezen.propick.bookmark.dto.BookmarksDTO;
import com.ezen.propick.bookmark.service.BookmarksService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bookmarks")
public class BookmarksController {

    private final BookmarksService bookmarksService;

    public BookmarksController(BookmarksService bookmarksService) {
        this.bookmarksService = bookmarksService;
    }

    @GetMapping("/createdBy/{createdBy}")
    public ResponseEntity<List<BookmarksDTO>> getBookmarksByCreatedBy(@PathVariable String createdBy) {
        List<BookmarksDTO> bookmarks = bookmarksService.getBookmarksByCreatedBy(createdBy);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    public ResponseEntity<BookmarksDTO> createBookmark(@RequestBody BookmarksDTO bookmarkDTO) {
        BookmarksDTO createdBookmark = bookmarksService.addBookmark(bookmarkDTO);
        return ResponseEntity.status(201).body(createdBookmark);
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Integer bookmarkId) {
        bookmarksService.removeBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam String createdBy, @RequestParam String url) {
        boolean isBookmarked = bookmarksService.isBookmarked(createdBy, url);
        return ResponseEntity.ok(isBookmarked);
    }

}
package com.ezen.propick.bookmark;

import com.ezen.propick.bookmark.dto.BookmarksDTO;
import com.ezen.propick.bookmark.entity.Bookmarks;
import com.ezen.propick.bookmark.repository.BookmarksRepository;
import com.ezen.propick.bookmark.service.BookmarksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarksServiceTest {

    @Mock
    private BookmarksRepository bookmarksRepository;

    @InjectMocks
    private BookmarksService bookmarksService;

    @Test
    void testAddBookmark_Success() {
        // Given
        BookmarksDTO dto = BookmarksDTO.builder()
                .title("Test Site")
                .url("https://test.com")
                .category("Study")
                .createdBy("user1")
                .build();

        Bookmarks bookmarks = Bookmarks.builder()
                .id(1)
                .title("Test Site")
                .url("https://test.com")
                .category("Study")
                .createdBy("user1")
                .createdAt(LocalDateTime.now())
                .build();

        when(bookmarksRepository.existsByCreatedByAndUrl("user1", "https://test.com")).thenReturn(false);
        when(bookmarksRepository.save(any(Bookmarks.class))).thenReturn(bookmarks);

        // When
        BookmarksDTO result = bookmarksService.addBookmark(dto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Site", result.getTitle());
        assertEquals("https://test.com", result.getUrl());
        assertEquals("Study", result.getCategory());
        assertEquals("user1", result.getCreatedBy());

        verify(bookmarksRepository, times(1)).existsByCreatedByAndUrl("user1", "https://test.com");
        verify(bookmarksRepository, times(1)).save(any(Bookmarks.class));
    }

    @Test
    void testAddBookmark_AlreadyExists() {
        // Given
        BookmarksDTO dto = BookmarksDTO.builder()
                .title("Test Site")
                .url("https://test.com")
                .category("Study")
                .createdBy("user1")
                .build();

        when(bookmarksRepository.existsByCreatedByAndUrl("user1", "https://test.com")).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> bookmarksService.addBookmark(dto));

        verify(bookmarksRepository, times(1)).existsByCreatedByAndUrl("user1", "https://test.com");
        verify(bookmarksRepository, never()).save(any(Bookmarks.class));
    }

    @Test
    void testGetBookmarksByCreatedBy_Success() {
        // Given
        Bookmarks bookmarks = Bookmarks.builder()
                .id(1)
                .title("Test Site")
                .url("https://test.com")
                .category("Study")
                .createdBy("user1")
                .createdAt(LocalDateTime.now())
                .build();

        List<Bookmarks> bookmarksList = Arrays.asList(bookmarks);

        when(bookmarksRepository.findByCreatedBy("user1")).thenReturn(bookmarksList);

        // When
        List<BookmarksDTO> result = bookmarksService.getBookmarksByCreatedBy("user1");

        // Then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Test Site", result.get(0).getTitle());
        assertEquals("https://test.com", result.get(0).getUrl());
        assertEquals("Study", result.get(0).getCategory());
        assertEquals("user1", result.get(0).getCreatedBy());

        verify(bookmarksRepository, times(1)).findByCreatedBy("user1");
    }

    @Test
    void testGetBookmarksByCreatedBy_EmptyList() {
        // Given
        when(bookmarksRepository.findByCreatedBy("user1")).thenReturn(Collections.emptyList());

        // When
        List<BookmarksDTO> result = bookmarksService.getBookmarksByCreatedBy("user1");

        // Then
        assertTrue(result.isEmpty());

        verify(bookmarksRepository, times(1)).findByCreatedBy("user1");
    }

    @Test
    void testRemoveBookmark_Success() {
        // Given
        when(bookmarksRepository.existsById(1)).thenReturn(true);

        // When
        bookmarksService.removeBookmark(1);

        // Then
        verify(bookmarksRepository, times(1)).existsById(1);
        verify(bookmarksRepository, times(1)).deleteById(1);
    }

    @Test
    void testRemoveBookmark_NotFound() {
        // Given
        when(bookmarksRepository.existsById(1)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookmarksService.removeBookmark(1));

        verify(bookmarksRepository, times(1)).existsById(1);
        verify(bookmarksRepository, never()).deleteById(1);
    }

    @Test
    void testIsBookmarked_True() {
        // Given
        when(bookmarksRepository.existsByCreatedByAndUrl("user1", "https://test.com")).thenReturn(true);

        // When
        boolean result = bookmarksService.isBookmarked("user1", "https://test.com");

        // Then
        assertTrue(result);

        verify(bookmarksRepository, times(1)).existsByCreatedByAndUrl("user1", "https://test.com");
    }

    @Test
    void testIsBookmarked_False() {
        // Given
        when(bookmarksRepository.existsByCreatedByAndUrl("user1", "https://test.com")).thenReturn(false);

        // When
        boolean result = bookmarksService.isBookmarked("user1", "https://test.com");

        // Then
        assertFalse(result);

        verify(bookmarksRepository, times(1)).existsByCreatedByAndUrl("user1", "https://test.com");
    }
}
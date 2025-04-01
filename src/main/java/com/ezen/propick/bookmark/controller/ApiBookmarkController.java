package com.ezen.propick.bookmark.controller;

import com.ezen.propick.auth.AuthDetails;
import com.ezen.propick.bookmark.service.BookmarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
public class ApiBookmarkController {

    private static final Logger logger = LoggerFactory.getLogger(ApiBookmarkController.class);

    private final BookmarkService bookmarkService;

    public ApiBookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    private Integer getCurrentUserNo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                logger.warn("No authenticated user found. Authentication: {}", authentication);
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof AuthDetails) {
                Integer userNo = ((AuthDetails) principal).getUserNo();
                logger.debug("Authenticated user found. userNo: {}", userNo);
                return userNo;
            }
            logger.warn("Principal is not an instance of AuthDetails. Principal: {}", principal);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving current userNo: {}", e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleBookmark(@RequestBody Map<String, Object> request) {
        logger.debug("Toggle request received: {}", request);
        logger.debug("Received toggle request: {}", request); // DEBUG로 변경
        Integer userNo = getCurrentUserNo();
        logger.debug("Current userNo: {}", userNo);
        if (userNo == null) {
            logger.warn("User is not authenticated for request: {}", request);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "User is not authenticated"));
        }

        Integer productId;
        try {
            productId = Integer.parseInt(request.get("productId").toString());
        } catch (NumberFormatException e) {
            logger.error("Invalid productId: {}", request.get("productId"), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid productId"));
        }

        boolean isBookmarked;
        try {
            isBookmarked = Boolean.parseBoolean(request.get("isBookmarked").toString());
        } catch (Exception e) {
            logger.error("Invalid isBookmarked value: {}", request.get("isBookmarked"), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid isBookmarked value"));
        }

        logger.info("Toggling bookmark for userNo: {}, productId: {}, isBookmarked: {}", userNo, productId, isBookmarked);

        boolean success = bookmarkService.toggleBookmark(userNo, productId, !isBookmarked);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (!success) {
            response.put("message", "Failed to toggle bookmark");
        }
        return ResponseEntity.ok(response);
    }
}
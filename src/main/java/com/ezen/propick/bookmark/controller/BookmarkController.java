package com.ezen.propick.bookmark.controller;

import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("user")
@Controller
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String userId = ((UserDetails) principal).getUsername();
            System.out.println("SecurityContextHolder에서 가져온 userId: " + userId);
            return userId;
        }
        System.out.println("SecurityContextHolder에서 userId를 가져올 수 없음: principal=" + principal);
        return null;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBookmark(@RequestParam("productId") Integer productId) {
        Map<String, Object> response = new HashMap<>();
        String userId = getCurrentUserId();

        if (userId == null) {
            response.put("success", false);
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        BookmarkDTO bookmarkDTO = BookmarkDTO.builder()
                .userId(userId)
                .productId(productId)
                .bookmarkStatus("ACTIVE")
                .build();

        try {
            BookmarkDTO savedBookmark = bookmarkService.addBookmark(bookmarkDTO);
            response.put("success", true);
            response.put("message", "북마크가 추가되었습니다!");
            response.put("bookmark", savedBookmark);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeBookmark(@PathVariable("productId") Integer productId) {
        Map<String, Object> response = new HashMap<>();
        String userId = getCurrentUserId();

        if (userId == null) {
            response.put("success", false);
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            bookmarkService.removeBookmark(userId, productId);
            response.put("success", true);
            response.put("message", "북마크가 해제되었습니다!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "북마크 해제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public String getBookmarkList(Model model) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return "redirect:/user/login";
        }
        System.out.println("현재 사용자 ID: " + userId);
        List<ProductListDTO> bookmarkedProducts = bookmarkService.getBookmarkedProducts(userId);
        System.out.println("북마크된 상품 개수: " + (bookmarkedProducts != null ? bookmarkedProducts.size() : "NULL"));
        System.out.println("북마크된 상품 리스트: " + bookmarkedProducts);
        model.addAttribute("bookmarkedProducts", bookmarkedProducts != null ? bookmarkedProducts : List.of());
        return "main/favorite";
    }

    @GetMapping("/mypage/bookmark")
    public String getBookmarkListRedirect() {
        return "redirect:/bookmark/list";
    }
}
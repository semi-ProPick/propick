package com.ezen.propick.bookmark.controller;

import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/add")
    public String addBookmark(HttpSession session,
                              @RequestParam("productId") Integer productId,
                              RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId"); // String 그대로 사용
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        BookmarkDTO bookmarkDTO = BookmarkDTO.builder()
                .userNo(userId) // Integer 대신 String으로
                .productId(productId)
                .bookmarkStatus("ACTIVE")
                .build();
        try {
            BookmarkDTO result = bookmarkService.addBookmark(bookmarkDTO);
            redirectAttributes.addFlashAttribute("message", "북마크가 추가되었습니다!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeBookmark(HttpSession session,
                                                              @RequestParam("productId") Integer productId) {
        String userId = (String) session.getAttribute("userId"); // Integer 대신 String
        Map<String, Object> response = new HashMap<>();
        if (userId == null) {
            response.put("success", false);
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.badRequest().body(response);
        }
        // 나머지 로직은 서비스에 맞게 조정
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
    @GetMapping("/top3")
    @ResponseBody
    public ResponseEntity<List<ProductListDTO>> getTop3BookmarkedProducts() {
        List<ProductListDTO> top3Products = bookmarkService.getTop3BookmarkedProducts();
        return ResponseEntity.ok(top3Products);
    }

    @GetMapping("/list")
    public String getBookmarkList(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId"); // Integer -> String

        if (userId == null) {
            return "redirect:/user/login";
        }

        List<ProductListDTO> bookmarkedProducts = bookmarkService.getBookmarkedProducts(userId);
        model.addAttribute("bookmarkedProducts", bookmarkedProducts);

        return "main/favorite";
    }
}
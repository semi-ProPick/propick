package com.ezen.propick.main.controller;

import com.ezen.propick.bookmark.service.BookmarkService;
import com.ezen.propick.product.dto.ProductDTO;
import com.ezen.propick.product.dto.ProductListDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MypageController {

    private final BookmarkService bookmarkService;

    public MypageController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/mypage")
    public String mainPage() {
        return "main/mypage";
    }

    @GetMapping("/mypage/bookmark")
    public String getBookmarks(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId"); // Integer -> String
        System.out.println("User ID from session: " + userId);
        if (userId == null) {
            System.out.println("User ID is null. Redirecting to login page.");
            return "/main/favorite";
        }
        List<ProductListDTO> bookmarkedProducts = bookmarkService.getBookmarkedProducts(userId);
        System.out.println("Bookmarked Products: " + bookmarkedProducts);
        model.addAttribute("bookmarkedProducts", bookmarkedProducts);
        return "main/favorite";
    }
}
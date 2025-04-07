package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.AdminUserModifyDTO;
import com.ezen.propick.user.dto.AfterUserInfoDTO;
import com.ezen.propick.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

//@Profile("admin") // 제거 또는 아래와 같이 변경
@Profile({"admin", "user"}) // admin과 user 프로필 모두에서 동작
@Controller
@RequestMapping("/index")
@RequiredArgsConstructor
public class adminController {
    private final UserService userService;
    private final UserRepository userRepository;

    @DeleteMapping("/delete/{userId}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("삭제 완료");
    }

    @GetMapping("/update")
    public String updateUser(@RequestParam("userId") String userId, Model model) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            model.addAttribute("error", "유저 정보를 찾을 수 없습니다.");
        }
        return "management/user_update";
    }

    @PostMapping("/update")
    public String processUserUpdate(@RequestParam("userId") String userId,
                                    @RequestParam("userName") String userName,
                                    @RequestParam("userPhone") String userPhone,
                                    Model model) {
        boolean isUpdated = userService.changeUserInfo(userId, userName, userPhone);
        if (isUpdated) {
            return "redirect:/index";
        } else {
            model.addAttribute("error", "회원 정보 수정에 실패하였습니다.");
            return "management/user_update";
        }
    }

    @PostMapping("")
    public String searchUsers(@RequestParam String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<User> users = userService.searchUsers(keyword, page, size);
        model.addAttribute("members", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "/management/user";
    }

    @GetMapping("")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        model.addAttribute("members", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        return "/management/user";
    }
}
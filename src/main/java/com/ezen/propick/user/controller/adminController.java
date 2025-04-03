package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.AdminUserModifyDTO;
import com.ezen.propick.user.dto.AfterUserInfoDTO;
import org.springframework.ui.Model;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@Profile("admin")
@Controller
@RequestMapping
@RequiredArgsConstructor
public class adminController {

    private final UserService userService;

    @GetMapping
    public String getUsers(Model model) {
        List<User> members = userService.findAllUsers();
        model.addAttribute("members", members);
        return "management/user";
    }

    @DeleteMapping("/delete/{userId}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("삭제 완료");
    }


    //유저 업데이트 페이지 반환
    @GetMapping("/update")
    public String updateUser(@RequestParam("userId") String userId, Model model) {
        // userId로 유저 정보 조회
        Optional<User> user = userService.getUserById(userId);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            model.addAttribute("error", "유저 정보를 찾을 수 없습니다.");
        }

        return "management/user_update";
    }


    @PostMapping("/update")
    public String updateUserInfo(@ModelAttribute User user) {
        System.out.println("Received Data - userId: " + user.getUserId() +
                ", userName: " + user.getUserName() +
                ", userPhone: " + user.getUserPhone());
        userService.changeUserInfo(user.getUserId(), user.getUserName(), user.getUserPhone());
        return "redirect:/"; // 수정 후 회원 목록 페이지로 이동
    }
}
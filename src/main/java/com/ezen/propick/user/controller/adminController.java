package com.ezen.propick.user.controller;

import org.springframework.ui.Model;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}

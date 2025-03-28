package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "/main/login";
    }

//@PostMapping("/login")
//public String login(@RequestParam("userId") String userId, @RequestParam("userPwd") String userPwd) {
//    // 받은 값 확인
//    System.out.println("userId: " + userId);
//    System.out.println("userPwd: " + userPwd);
//
//    // 로그인 처리를 진행
//    return "redirect:/"; // 로그인 후 리다이렉트할 URL
//}

    //     회원가입 페이지를 보여주는 메서드
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO()); // Model에 memberDTO 추가
        return "/main/join";  // join.html 뷰를 반환
    }

    @PostMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO, BindingResult bindingResult) {
        userService.createMember(memberDTO);
        return "redirect:/";
    }

}

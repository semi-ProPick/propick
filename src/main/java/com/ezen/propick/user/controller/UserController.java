package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

    // 회원가입 페이지를 보여주는 메서드
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO()); // Model에 memberDTO 추가
        return "/main/join";  // join.html 뷰를 반환
    }

    // 회원가입을 처리하는 메서드
//    @PostMapping("/signUp")
//    public void createMember(MemberDTO memberDTO) {
//        userService.createMember(memberDTO);
//        return "redirect:/success"; // 회원가입 완료 후 리디렉션 (예: 성공 페이지)
//    }


//    @GetMapping("/test")
//    public String join(){
//        return "test";
//    }

//    @GetMapping("/join")
//    public String join1(){
//        return "main/join";
//    }



}

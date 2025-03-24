package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

//     회원가입 페이지를 보여주는 메서드
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO()); // Model에 memberDTO 추가
        return "/main/join";  // join.html 뷰를 반환
    }

//    로그인 페이지를 보여주는 메서드
    @GetMapping("/login")
    public String login(){
        return"/main/login";
    }

    @PostMapping("/join")
    public String create(MemberDTO memberDTO) {
        // userService의 save 또는 create 메서드 호출
        userService.createMember(memberDTO);  // 또는 userService.create(memberDTO);
        // 회원가입 후 홈페이지로 리다이렉트
        return "redirect:/main/login";
    }


}
